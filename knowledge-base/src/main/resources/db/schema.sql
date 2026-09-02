SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE `document` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文档标题',
                            `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文档内容(Markdown)',
                            `parent_id` bigint DEFAULT '0',
                            `user_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` tinyint(1) NOT NULL DEFAULT '0',
                            PRIMARY KEY (`id`),
                            KEY `idx_user_parent` (`user_id`,`parent_id`),
                            KEY `idx_title` (`title`),
                            KEY `idx_content` (`content`(100))
) ENGINE=InnoDB AUTO_INCREMENT=2091882678302900227 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `document_tag` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `document_id` bigint NOT NULL,
                                `tag_id` bigint NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_doc_tag` (`document_id`,`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2090246059304964099 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tag` (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
                       `user_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2090245940992036866 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                        `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密)',
                        `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
                        `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2090406827421900803 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

