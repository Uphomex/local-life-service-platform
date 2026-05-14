
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `nickname` VARCHAR(100) COMMENT '昵称',
    `avatar` VARCHAR(500) COMMENT '头像',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/MERCHANT/RIDER/ADMIN',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_username` (`username`),
    INDEX `idx_user_phone` (`phone`),
    INDEX `idx_user_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `merchant` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商家ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商家名称',
    `logo` VARCHAR(500) COMMENT '商家logo',
    `address` VARCHAR(500) NOT NULL COMMENT '地址',
    `longitude` DECIMAL(15,12) NOT NULL COMMENT '经度',
    `latitude` DECIMAL(15,12) NOT NULL COMMENT '纬度',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `category` VARCHAR(50) NOT NULL COMMENT '分类',
    `business_license` VARCHAR(500) NOT NULL COMMENT '营业执照',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/ACTIVE/INACTIVE',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '配送费',
    `min_order_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '起送价',
    `delivery_time` INT DEFAULT 30 COMMENT '预计配送时间（分钟）',
    `rating` DECIMAL(3,2) DEFAULT 0 COMMENT '评分',
    `review_count` INT DEFAULT 0 COMMENT '评价数量',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_merchant_status` (`status`),
    INDEX `idx_merchant_category` (`category`),
    INDEX `idx_merchant_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `image` VARCHAR(500) COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) COMMENT '原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sold_count` INT DEFAULT 0 COMMENT '销量',
    `category` VARCHAR(50) COMMENT '分类',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_product_merchant` (`merchant_id`),
    INDEX `idx_product_status` (`status`),
    INDEX `idx_product_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS `product_attribute` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '属性名',
    `value` VARCHAR(500) NOT NULL COMMENT '属性值',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_attr_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

CREATE TABLE IF NOT EXISTS `product_sku` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SKU ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_code` VARCHAR(100) COMMENT 'SKU编码',
    `spec_values` VARCHAR(500) NOT NULL COMMENT '规格值',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `image` VARCHAR(500) COMMENT '图片',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_sku_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `rider_id` BIGINT COMMENT '骑手ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总额',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '配送费',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING/PAID/DELIVERING/COMPLETED/CANCELLED',
    `pay_status` VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID/PAID/REFUNDED',
    `pay_method` VARCHAR(20) DEFAULT 'WECHAT' COMMENT '支付方式：WECHAT/ALIPAY',
    `receiver_name` VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(500) NOT NULL COMMENT '收货地址',
    `receiver_longitude` DECIMAL(15,12) NOT NULL COMMENT '收货经度',
    `receiver_latitude` DECIMAL(15,12) NOT NULL COMMENT '收货纬度',
    `pay_time` DATETIME COMMENT '支付时间',
    `delivery_time` DATETIME COMMENT '配送时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_user` (`user_id`),
    INDEX `idx_order_merchant` (`merchant_id`),
    INDEX `idx_order_rider` (`rider_id`),
    INDEX `idx_order_status` (`status`),
    INDEX `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT COMMENT 'SKU ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `sku_spec` VARCHAR(200) COMMENT '规格说明',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `image` VARCHAR(500) COMMENT '商品图片',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_item_order` (`order_id`),
    INDEX `idx_order_item_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

CREATE TABLE IF NOT EXISTS `rider` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '骑手ID',
    `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    `real_name` VARCHAR(100) NOT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/ACTIVE/INACTIVE',
    `longitude` DECIMAL(15,12) DEFAULT 0 COMMENT '经度',
    `latitude` DECIMAL(15,12) DEFAULT 0 COMMENT '纬度',
    `order_count` INT DEFAULT 0 COMMENT '完成订单数',
    `rating` DECIMAL(3,2) DEFAULT 0 COMMENT '评分',
    `earnings` DECIMAL(10,2) DEFAULT 0 COMMENT '累计收入',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_rider_user` (`user_id`),
    INDEX `idx_rider_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手表';

CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `product_id` BIGINT COMMENT '商品ID',
    `rating` INT NOT NULL COMMENT '评分（1-5）',
    `content` TEXT COMMENT '评价内容',
    `images` VARCHAR(1000) COMMENT '图片URL，逗号分隔',
    `reply` TEXT COMMENT '商家回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DELETED',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_review_user` (`user_id`),
    INDEX `idx_review_merchant` (`merchant_id`),
    INDEX `idx_review_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

CREATE TABLE IF NOT EXISTS `cart_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车项ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT COMMENT 'SKU ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_cart_user` (`user_id`),
    INDEX `idx_cart_merchant` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
