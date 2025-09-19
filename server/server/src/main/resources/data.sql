-- Demo data initialization for H2 database

-- Insert demo categories
INSERT INTO `b_classification` (`title`, `create_time`) VALUES
('文学类', '2024-01-01 12:00:00'),
('科技类', '2024-01-01 12:00:00'),
('历史类', '2024-01-01 12:00:00'),
('教育类', '2024-01-01 12:00:00');

-- Insert demo book data
INSERT INTO `b_thing` (`title`, `cover`, `description`, `price`, `status`, `create_time`, `repertory`, `score`, `pv`, `recommend_count`, `wish_count`, `collect_count`, `classification_id`) VALUES
('Spring Boot实战指南', 'spring-boot.jpg', '一本全面介绍Spring Boot框架的实战指南，包含大量案例和最佳实践。适合Java开发者学习现代微服务架构。', '89.00', '1', '2024-01-01 12:00:00', '100', '4.8', '1250', '45', '32', '28', 2),
('人工智能原理与应用', 'ai-book.jpg', '深入浅出讲解人工智能的基本原理和实际应用，涵盖机器学习、深度学习等热门技术。', '125.00', '1', '2024-01-02 12:00:00', '85', '4.7', '980', '67', '43', '35', 2),
('中国通史', 'history-book.jpg', '权威的中国历史通史读物，从古代文明到现代社会的完整历史脉络。', '158.00', '1', '2024-01-03 12:00:00', '60', '4.9', '756', '78', '56', '67', 3),
('文学经典选读', 'literature.jpg', '精选中外文学经典作品，帮助读者提升文学素养和审美能力。', '75.00', '1', '2024-01-04 12:00:00', '120', '4.6', '642', '34', '29', '23', 1),
('现代教育心理学', 'education.jpg', '探讨现代教育理念和心理学在教育中的应用，为教育工作者提供理论指导。', '95.00', '1', '2024-01-05 12:00:00', '75', '4.5', '534', '28', '18', '15', 4),
('Vue.js前端开发实战', 'vue-book.jpg', '全面介绍Vue.js框架的前端开发实战教程，包含组件化开发和状态管理。', '99.00', '1', '2024-01-06 12:00:00', '90', '4.7', '892', '52', '38', '31', 2);

-- Insert demo user data
INSERT INTO `b_user` (`username`, `password`, `role`, `status`, `nickname`, `avatar`, `mobile`, `email`, `gender`, `description`, `create_time`, `score`, `push_email`, `push_switch`, `admin_token`, `token`) VALUES
('admin', '123456', '1', '1', '管理员', 'admin-avatar.jpg', '13800138000', 'admin@bookstore.com', '1', '系统管理员', '2024-01-01 10:00:00', 100, 'admin@bookstore.com', 1, 'admin-token-123', 'admin-token-123'),
('user1', '123456', '2', '1', '读书爱好者', 'user1-avatar.jpg', '13800138001', 'user1@email.com', '1', '热爱阅读的用户', '2024-01-02 10:00:00', 85, 'user1@email.com', 1, '', 'user1-token-456');