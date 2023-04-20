/*
SQLyog Ultimate v9.60 
MySQL - 5.7.33-log : Database - course_arrange_v3
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`course_arrange_v3` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `course_arrange_v3`;

/*Table structure for table `nin_arrange` */

DROP TABLE IF EXISTS `nin_arrange`;

CREATE TABLE `nin_arrange` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `career_id` bigint(20) NOT NULL COMMENT '专业id',
  `class_id` bigint(20) DEFAULT NULL COMMENT '班级id',
  `teach_class_id` bigint(20) DEFAULT NULL COMMENT '教学班id',
  `teacher_id` bigint(20) DEFAULT NULL COMMENT '教师id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `house_id` bigint(20) DEFAULT NULL COMMENT '教室id',
  `must` tinyint(4) NOT NULL COMMENT '是否选修，0-选修，1-必修',
  `weekly` tinyint(4) NOT NULL COMMENT '单双周，0每周，1单周，2双周',
  `start_time` tinyint(4) NOT NULL DEFAULT '1' COMMENT '开始周次',
  `end_time` tinyint(4) NOT NULL DEFAULT '16' COMMENT '结束周次',
  `week` tinyint(4) DEFAULT NULL COMMENT '星期',
  `pitch_num` tinyint(4) DEFAULT NULL COMMENT '节数',
  `people_num` int(10) NOT NULL COMMENT '人数',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`),
  KEY `idx_teach_class` (`class_id`,`teach_class_id`),
  KEY `idx_course` (`course_id`),
  KEY `idx_tea` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_career` */

DROP TABLE IF EXISTS `nin_career`;

CREATE TABLE `nin_career` (
  `id` bigint(20) NOT NULL COMMENT '专业id，0-选修，-1-补课',
  `college` varchar(20) NOT NULL COMMENT '学院',
  `career_name` varchar(20) NOT NULL COMMENT '专业名称',
  `class_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '班级数量',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_career_course` */

DROP TABLE IF EXISTS `nin_career_course`;

CREATE TABLE `nin_career_course` (
  `id` bigint(20) NOT NULL COMMENT '专业-课程id',
  `career_id` bigint(20) NOT NULL COMMENT '专业id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_class` */

DROP TABLE IF EXISTS `nin_class`;

CREATE TABLE `nin_class` (
  `id` bigint(20) NOT NULL COMMENT '班级id',
  `career_id` bigint(20) DEFAULT NULL COMMENT '专业id',
  `class_name` varchar(20) NOT NULL COMMENT '班级名称',
  `people_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '班级人数',
  `course_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '已有课程数',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_course` */

DROP TABLE IF EXISTS `nin_course`;

CREATE TABLE `nin_course` (
  `id` bigint(20) unsigned NOT NULL COMMENT '课程id',
  `course_name` varchar(20) NOT NULL COMMENT '课程名称',
  `house_type` tinyint(4) NOT NULL COMMENT '课程需要的教室类型.0-普通教室，1-机房，2-实验室，3-课外，4-网课',
  `must` tinyint(4) NOT NULL COMMENT '是否必修，0-选修，1-必修',
  `course_time` tinyint(4) NOT NULL COMMENT '一学期要上的节数，8,16,32,48,64',
  `start_time` tinyint(4) NOT NULL DEFAULT '1' COMMENT '开始周次',
  `end_time` tinyint(4) NOT NULL DEFAULT '16' COMMENT '结束周次',
  `week_time` tinyint(4) NOT NULL DEFAULT '16' COMMENT '几周结束，即在几周内上完',
  `max_class_num` tinyint(4) DEFAULT NULL COMMENT '最多一起上课的数量',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_house` */

DROP TABLE IF EXISTS `nin_house`;

CREATE TABLE `nin_house` (
  `id` bigint(20) unsigned NOT NULL COMMENT '教室id',
  `house_name` varchar(20) NOT NULL COMMENT '教室名称',
  `house_type` tinyint(4) NOT NULL COMMENT '教室类型,0-普通教室，1-机房，2-实验室，3-课外，4网课',
  `seat` tinyint(4) unsigned DEFAULT NULL COMMENT '教室座位',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_message` */

DROP TABLE IF EXISTS `nin_message`;

CREATE TABLE `nin_message` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '主键id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `is_consent` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '是否同意-1-无需操作,0-未操作,1-同意,2-拒绝',
  `msg` varchar(500) NOT NULL DEFAULT '""' COMMENT '消息内容',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`),
  KEY `idx_read_uid` (`user_id`,`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_setting` */

DROP TABLE IF EXISTS `nin_setting`;

CREATE TABLE `nin_setting` (
  `id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '设置id',
  `course_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '开放的课程id',
  `course_name` varchar(20) NOT NULL DEFAULT '""' COMMENT '课程名称',
  `user_type` varchar(10) NOT NULL DEFAULT 'teacher' COMMENT '用户类型',
  `open_state` tinyint(4) NOT NULL DEFAULT '1' COMMENT '开放状态 0-未开放，1-开放中, 2-已结束',
  `open_time` datetime DEFAULT NULL COMMENT '开放时间',
  `close_time` datetime DEFAULT NULL COMMENT '结束时间',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_student` */

DROP TABLE IF EXISTS `nin_student`;

CREATE TABLE `nin_student` (
  `id` bigint(20) unsigned NOT NULL COMMENT '学生id',
  `student_name` varchar(20) NOT NULL COMMENT '学生名称',
  `student_code` varchar(20) NOT NULL COMMENT '学生账号',
  `student_password` varchar(40) NOT NULL COMMENT '学生密码',
  `class_id` bigint(20) NOT NULL COMMENT '班级id',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_student_course` */

DROP TABLE IF EXISTS `nin_student_course`;

CREATE TABLE `nin_student_course` (
  `id` bigint(20) unsigned NOT NULL COMMENT '学生-课程id',
  `student_id` bigint(20) NOT NULL COMMENT '学生id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `take_class_id` bigint(20) NOT NULL COMMENT '学生选修课程的班级id',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_teach_class` */

DROP TABLE IF EXISTS `nin_teach_class`;

CREATE TABLE `nin_teach_class` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `teach_class_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '教学班id',
  `class_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '班级id',
  `class_name` varchar(20) NOT NULL DEFAULT '""' COMMENT '班级名称',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`),
  KEY `idx_teach` (`teach_class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_teacher` */

DROP TABLE IF EXISTS `nin_teacher`;

CREATE TABLE `nin_teacher` (
  `id` bigint(20) unsigned NOT NULL COMMENT '教师id',
  `teacher_name` varchar(20) NOT NULL COMMENT '教师名称',
  `teacher_code` varchar(20) NOT NULL COMMENT '教师账号',
  `teacher_password` varchar(40) NOT NULL COMMENT '密码',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nin_teacher_course` */

DROP TABLE IF EXISTS `nin_teacher_course`;

CREATE TABLE `nin_teacher_course` (
  `id` bigint(20) unsigned NOT NULL COMMENT '教师-课程id',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师id',
  `course_id` bigint(20) NOT NULL COMMENT '课程id',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建人id',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
