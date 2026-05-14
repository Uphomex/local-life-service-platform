
INSERT INTO `user` (`username`, `password`, `phone`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '13800138000', '管理员', 'ADMIN', 'ACTIVE'),
('user001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '13800138001', '用户1', 'USER', 'ACTIVE'),
('merchant001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '13800138002', '商家1', 'MERCHANT', 'ACTIVE'),
('rider001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '13800138003', '骑手1', 'RIDER', 'ACTIVE');

INSERT INTO `merchant` (`name`, `logo`, `address`, `longitude`, `latitude`, `phone`, `category`, `business_license`, `status`, `delivery_fee`, `min_order_amount`, `delivery_time`, `rating`, `review_count`) VALUES
('麦当劳', 'https://example.com/mcdonalds.png', '北京市朝阳区xxx路1号', 116.4731, 39.9932, '010-12345678', '快餐', 'license_mcdonalds.jpg', 'ACTIVE', 5.00, 20.00, 25, 4.5, 1200),
('肯德基', 'https://example.com/kfc.png', '北京市海淀区xxx路2号', 116.3084, 39.9975, '010-87654321', '快餐', 'license_kfc.jpg', 'ACTIVE', 6.00, 25.00, 30, 4.4, 980),
('星巴克', 'https://example.com/starbucks.png', '北京市西城区xxx路3号', 116.3972, 39.9087, '010-11223344', '饮品', 'license_starbucks.jpg', 'ACTIVE', 8.00, 30.00, 20, 4.7, 850);

INSERT INTO `product` (`merchant_id`, `name`, `description`, `image`, `price`, `original_price`, `stock`, `sold_count`, `category`, `status`, `sort_order`) VALUES
(1, '汉堡套餐', '经典牛肉汉堡+薯条+可乐', 'https://example.com/burger.jpg', 29.90, 39.90, 100, 2500, '套餐', 'ACTIVE', 1),
(1, '炸鸡套餐', '香辣鸡翅5块+薯条+可乐', 'https://example.com/chicken.jpg', 35.90, 45.90, 80, 1800, '套餐', 'ACTIVE', 2),
(2, '原味鸡', '外酥里嫩的原味炸鸡', 'https://example.com/original.jpg', 12.00, 15.00, 200, 5000, '单品', 'ACTIVE', 1),
(2, '香辣鸡腿堡', '香辣鸡腿肉+生菜+沙拉酱', 'https://example.com/spicy.jpg', 19.90, 24.90, 150, 3200, '汉堡', 'ACTIVE', 2),
(3, '拿铁咖啡', '香浓拿铁，经典口味', 'https://example.com/latte.jpg', 28.00, 32.00, 50, 4500, '咖啡', 'ACTIVE', 1),
(3, '抹茶拿铁', '日式抹茶+牛奶', 'https://example.com/matcha.jpg', 32.00, 36.00, 40, 2800, '咖啡', 'ACTIVE', 2);
