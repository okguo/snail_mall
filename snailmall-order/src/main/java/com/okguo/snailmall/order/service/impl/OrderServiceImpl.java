package com.okguo.snailmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.okguo.common.exception.BizCodeEnum;
import com.okguo.common.exception.RRException;
import com.okguo.common.to.OrderTo;
import com.okguo.common.to.mq.SeckillOrderTo;
import com.okguo.common.utils.PageUtils;
import com.okguo.common.utils.Query;
import com.okguo.common.utils.R;
import com.okguo.common.vo.MemberVO;
import com.okguo.snailmall.order.constant.OrderConstant;
import com.okguo.snailmall.order.dao.OrderDao;
import com.okguo.snailmall.order.entity.OrderEntity;
import com.okguo.snailmall.order.entity.OrderItemEntity;
import com.okguo.snailmall.order.entity.PaymentInfoEntity;
import com.okguo.snailmall.order.enume.OrderStatusEnum;
import com.okguo.snailmall.order.feign.CartFeignService;
import com.okguo.snailmall.order.feign.MemberFeignService;
import com.okguo.snailmall.order.feign.ProductFeignService;
import com.okguo.snailmall.order.feign.WareFeignService;
import com.okguo.snailmall.order.interceptor.LoginUserInterceptor;
import com.okguo.snailmall.order.service.OrderItemService;
import com.okguo.snailmall.order.service.OrderService;
import com.okguo.snailmall.order.service.PaymentInfoService;
import com.okguo.snailmall.order.to.OrderCreateTo;
import com.okguo.snailmall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private final ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberVO memberVO = LoginUserInterceptor.threadLocal.get();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //异步编排保证每个线程都共享cookie
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = memberFeignService.queryMemberAddressByMemberId(memberVO.getId());
            if (r.getCode() == 0) {
                List<MemberAddressVo> addressList = r.getData("list", new TypeReference<List<MemberAddressVo>>() {
                });
                orderConfirmVo.setAddress(addressList);
            }
        }, executor);

        CompletableFuture<Void> orderItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r1 = cartFeignService.currentUserCartItems(memberVO.getId());
            if (r1.getCode() == 0) {
                List<OrderItemVo> cartItems = r1.getData("cartItems", new TypeReference<List<OrderItemVo>>() {
                });
                orderConfirmVo.setItems(cartItems);
            }
        }, executor).thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<Long> collect = orderConfirmVo.getItems().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.hasStock(collect);
            List<SkuStockVo> data = r.getData("data", new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null && data.size() > 0) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        }, executor);

        orderConfirmVo.setIntegration(memberVO.getIntegration());

        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberVO.getId().toString(), token, 30, TimeUnit.MINUTES);

        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(addressFuture, orderItemFuture).get();

        return orderConfirmVo;
    }

    /**
     * @param submitVo
     * @return
     * @GlobalTransactional
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        submitVoThreadLocal.set(submitVo);
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        MemberVO memberVO = LoginUserInterceptor.threadLocal.get();
        //1、验证令牌 (保证查询，验证，删除令牌为原子操作)

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //原子 查询，验证，删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberVO.getId().toString()), submitVo.getOrderToken());
//        String token = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberVO.getId());
//        if (StringUtils.isNotEmpty(token) && token.equals(submitVo.getOrderToken())) {
//        } else {
//            responseVo.setCode(1);
//        }
        if (result == 0L) {
            responseVo.setCode(1);
            return responseVo;
        }
        //2、创建订单
        OrderCreateTo order = createOrder(memberVO);
        //3.验证价格
        BigDecimal payAmount = order.getOrder().getPayAmount();
        BigDecimal payPrice = submitVo.getPayPrice();
        //价格误差
        if (Math.abs(payAmount.subtract(payPrice).doubleValue()) >= 0.01) {
            responseVo.setCode(2);
            return responseVo;
        }
        //4.保存订单数据
        saveOrderToDB(order);
        //5.锁定库存
        WareSkuLockVo lockVo = new WareSkuLockVo();
        lockVo.setOrderSn(order.getOrder().getOrderSn());
        List<OrderItemVo> collect = order.getOrderItems().stream().map(e -> {
            OrderItemVo vo = new OrderItemVo();
            vo.setSkuId(e.getSkuId());
            vo.setTitle(e.getSkuName());
            vo.setCount(e.getSkuQuantity());
            return vo;
        }).collect(Collectors.toList());
        lockVo.setLocks(collect);
        R r = wareFeignService.orderLock(lockVo);
        if (r.getCode() != 0) {
            responseVo.setCode(3);
            throw new RRException(BizCodeEnum.NO_STOCK_EXCEPTION.getMsg(), BizCodeEnum.NO_STOCK_EXCEPTION.getCode());
        }

        //TODO 订单创建成功发送消息
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", JSON.toJSONString(order.getOrder()));

        log.info("submitOrder->------");
        responseVo.setOrder(order.getOrder());
        return responseVo;
    }

    @Override
    public OrderEntity getOrderStatus(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        OrderEntity order = this.getById(orderEntity.getId());
        //如果订单为待付款状态则需要关单
        if (order.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            order.setStatus(OrderStatusEnum.CANCELED.getCode());
            this.updateById(order);
            //关闭订单成功后，向库存方发送请求，检查库存有没有释放
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(order, orderTo);
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
        }
    }

    @Override
    public PayVo queryOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));

        PayVo payVo = new PayVo();
        List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        payVo.setTotal_amount(orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberVO memberVO = LoginUserInterceptor.threadLocal.get();
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberVO.getId()).orderByDesc("id"));
        List<OrderEntity> collect = page.getRecords().stream().map(e -> {
            OrderEntity orderEntity = new OrderEntity();
            BeanUtils.copyProperties(e, orderEntity);
            List<OrderItemEntity> orderItemEntities = orderItemService.listByOrderSn(e.getOrderSn());
            orderEntity.setOrderItemEntities(orderItemEntities);
            return orderEntity;
        }).collect(Collectors.toList());
        page.setRecords(collect);

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1.支付流水
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(vo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(vo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        paymentInfo.setCallbackTime(vo.getNotify_time());
        paymentInfo.setSubject(vo.getSubject());
        paymentInfo.setPaymentStatus(vo.getTrade_status());
        paymentInfoService.save(paymentInfo);
        //2.修改订单状态

        if ("TRADE_SUCCESS".equals(vo.getTrade_status()) || "TRADE_FINISHED".equals(vo.getTrade_status())) {
            this.baseMapper.updateOrderStatus(vo.getOut_trade_no(), OrderStatusEnum.PAYED.getCode());
        }

        return "success";
    }

    @Override
    public void orderSeckillCreate(SeckillOrderTo seckillOrderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal amount = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal("" + seckillOrderTo.getNum()));
        orderEntity.setPayAmount(amount);
        this.save(orderEntity);

        //TODO 保存订单项信息
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderItemEntity.setSkuId(seckillOrderTo.getSkuId());
        orderItemEntity.setSkuPrice(seckillOrderTo.getSeckillPrice());
        orderItemEntity.setSkuQuantity(seckillOrderTo.getNum());
        orderItemEntity.setRealAmount(amount);
        orderItemService.save(orderItemEntity);
    }

    void saveOrderToDB(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        orderItemService.saveBatch(order.getOrderItems());

    }

    public OrderCreateTo createOrder(MemberVO memberVO) {
        OrderCreateTo createTo = new OrderCreateTo();
        //1.生成订单号
        OrderEntity orderEntity = buildOrderEntity();
        orderEntity.setMemberId(memberVO.getId());
        orderEntity.setMemberUsername(memberVO.getUsername());
        createTo.setOrder(orderEntity);
        //3.设置订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItemEntities(memberVO.getId(), orderEntity.getOrderSn());
        createTo.setOrderItems(orderItemEntities);
        //4.验证价格
        buildPrice(orderEntity, orderItemEntities);
        createTo.setFare(orderEntity.getFreightAmount());
        return createTo;
    }

    private void buildPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        int growth = 0;
        int inte = 0;
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            coupon = coupon.add(orderItemEntity.getCouponAmount());
            integration = integration.add(orderItemEntity.getIntegrationAmount());
            promotion = promotion.add(orderItemEntity.getPromotionAmount());
            total = total.add(orderItemEntity.getRealAmount());

            growth = growth + orderItemEntity.getGiftGrowth();
            inte = inte + orderItemEntity.getGiftIntegration();

        }
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setPromotionAmount(promotion);

        orderEntity.setGrowth(growth);
        orderEntity.setIntegration(inte);
    }

    private OrderEntity buildOrderEntity() {
        OrderEntity orderEntity = new OrderEntity();
        //订单号
        String orderSn = IdWorker.getTimeId();
        orderEntity.setOrderSn(orderSn);
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        R r = wareFeignService.getFee(orderSubmitVo.getAddrId());
        //2.设置收货人信息
        if (r.getCode() == 0) {
            FareVo fareVo = r.getData(new TypeReference<FareVo>() {
            });
            orderEntity.setFreightAmount(fareVo.getFare());
            orderEntity.setReceiverCity(fareVo.getAddress().getCity());
            orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
            orderEntity.setReceiverName(fareVo.getAddress().getName());
            orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
            orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());
            orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
            orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        }
        //设置状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        orderEntity.setDeleteStatus(0);
        orderEntity.setCreateTime(new Date());

        return orderEntity;
    }

    private List<OrderItemEntity> buildOrderItemEntities(Long memberId, String orderSn) {
        R r1 = cartFeignService.currentUserCartItems(memberId);
        if (r1.getCode() == 0) {
            List<OrderItemVo> cartItems = r1.getData("cartItems", new TypeReference<List<OrderItemVo>>() {
            });
            if (cartItems != null && cartItems.size() > 0) {
                return cartItems.stream().map(e -> {
                    OrderItemEntity orderItemEntity = buildOrderEntity(e);
                    orderItemEntity.setOrderSn(orderSn);
                    return orderItemEntity;
                }).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private OrderItemEntity buildOrderEntity(OrderItemVo orderVo) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1.订单信息
        //2.spu信息
        R r = productFeignService.querySpuBySkuId(orderVo.getSkuId());
        SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoVo.getId());
        orderItemEntity.setSpuName(spuInfoVo.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());

        //3.sku信息
        orderItemEntity.setSkuId(orderVo.getSkuId());
        orderItemEntity.setSkuName(orderVo.getTitle());
        orderItemEntity.setSkuPic(orderVo.getImage());
        orderItemEntity.setSkuPrice(orderVo.getPrice());
        orderItemEntity.setSkuQuantity(orderVo.getCount());
        orderItemEntity.setSkuAttrsVals(StringUtils.join(orderVo.getSkuAttr(), ";"));
        //4.优惠信息
        //5.积分信息
        orderItemEntity.setGiftIntegration(orderVo.getTotalPrice().intValue());
        orderItemEntity.setGiftGrowth(orderVo.getTotalPrice().intValue());
        //6.价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = origin
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }

}