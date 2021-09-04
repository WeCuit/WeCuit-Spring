/*==============================================================*/
/* DBMS name:      MySQL 5.7                                    */
/* Created on:     2021/8/20 17:25:40                           */
/*==============================================================*/


# HEADER
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO"; # 自增设置
SET FOREIGN_KEY_CHECKS=0;  # 取消外键约束


drop table if exists rb_dict;

drop table if exists rb_meta;

drop table if exists rb_picture;

drop table if exists rb_plugin;

drop table if exists wc_cdn_token;

drop table if exists wc_colleges;

drop table if exists wc_info;

drop table if exists wc_info_tag_relationship;

drop table if exists wc_options;

drop table if exists wc_sub;

drop table if exists wc_sub_template;

drop table if exists wc_tag_taxonomy;

drop table if exists wc_tags;

drop table if exists wc_users;

/*==============================================================*/
/* Table: rb_dict                                               */
/*==============================================================*/
create table rb_dict
(
   id                   bigint(20) not null auto_increment comment '词ID',
   keyword              varchar(20) not null comment '关键词',
   value                longtext comment '响应内容',
   primary key (id)
)
engine = InnoDB;

alter table rb_dict comment '机器人字典表';

/*==============================================================*/
/* Index: Index_Key                                             */
/*==============================================================*/
create unique index Index_Key on rb_dict
(
   keyword
);

/*==============================================================*/
/* Table: rb_meta                                               */
/*==============================================================*/
create table rb_meta
(
   id                   bigint(20) not null auto_increment comment '元ID',
   name                 varchar(255) not null comment '元数据名',
   value                longtext comment '元数据值',
   primary key (id)
)
engine = InnoDB;

alter table rb_meta comment '机器人元数据表';

/*==============================================================*/
/* Index: Index_Meta_Name                                       */
/*==============================================================*/
create unique index Index_Meta_Name on rb_meta
(
   name
);

/*==============================================================*/
/* Table: rb_picture                                            */
/*==============================================================*/
create table rb_picture
(
   id                   bigint(20) not null auto_increment comment '图片序号',
   img_id               varchar(255) comment '图片ID
            用于发送消息的ID',
   info                 json comment '图片信息',
   level                enum('0','1','2') comment '图片等级',
   primary key (id)
)
engine = InnoDB;

alter table rb_picture comment '机器人图片数据表';

/*==============================================================*/
/* Index: Index_picture_level                                   */
/*==============================================================*/
create index Index_picture_level on rb_picture
(
   level
);

/*==============================================================*/
/* Table: rb_plugin                                             */
/*==============================================================*/
create table rb_plugin
(
   id                   bigint(20) not null auto_increment comment '序号',
   name                 varchar(255) not null comment '插件名',
   config               json comment '插件配置',
   primary key (id)
)
engine = InnoDB;

alter table rb_plugin comment '机器人插件数据表';

/*==============================================================*/
/* Index: Index_Plugin_Name                                     */
/*==============================================================*/
create unique index Index_Plugin_Name on rb_plugin
(
   name
);

/*==============================================================*/
/* Table: wc_cdn_token                                          */
/*==============================================================*/
create table wc_cdn_token
(
   token                varchar(255) not null,
   time                 date,
   primary key (token)
)
engine = MEMORY;

/*==============================================================*/
/* Index: Index_token                                           */
/*==============================================================*/
create unique index Index_token on wc_cdn_token
(
   token
);

/*==============================================================*/
/* Table: wc_colleges                                           */
/*==============================================================*/
create table wc_colleges
(
   id                   bigint(20) not null auto_increment comment '学院ID',
   name                 varchar(50) comment '学院名',
   mark                 varchar(20) not null comment '学院标记',
   primary key (id)
)
engine = InnoDB;

alter table wc_colleges comment '学院表';

/*==============================================================*/
/* Index: Index_college_mark                                    */
/*==============================================================*/
create unique index Index_college_mark on wc_colleges
(
   mark
);

/*==============================================================*/
/* Table: wc_info                                               */
/*==============================================================*/
create table wc_info
(
   id                   bigint(20) not null auto_increment comment '信息ID',
   title                varchar(255) comment '信息标题',
   content              text comment '信息内容',
   type                 enum('html', 'file', 'link') comment '信息类型',
   primary key (id)
)
engine = InnoDB;

alter table wc_info comment '信息表';

/*==============================================================*/
/* Index: Index_title                                           */
/*==============================================================*/
create index Index_title on wc_info
(
   title
);

/*==============================================================*/
/* Table: wc_info_tag_relationship                              */
/*==============================================================*/
create table wc_info_tag_relationship
(
   id                   bigint(20) not null auto_increment comment '关联ID',
   info_id              bigint(20) not null comment '信息ID',
   college_id           bigint(20) not null comment '学院ID',
   tag_id               bigint(20) not null comment '标签ID',
   primary key (id)
)
engine = InnoDB;

alter table wc_info_tag_relationship comment '信息&标签&学院关联表';

/*==============================================================*/
/* Index: Index_ICT                                             */
/*==============================================================*/
create unique index Index_ICT on wc_info_tag_relationship
(
   info_id,
   college_id,
   tag_id
);

/*==============================================================*/
/* Table: wc_options                                            */
/*==============================================================*/
create table wc_options
(
   id                   bigint(20) not null auto_increment comment '配置ID',
   name                 varchar(100) not null comment '配置名',
   value                text comment '配置值',
   primary key (id)
)
engine = InnoDB;

alter table wc_options comment '系统配置';

/*==============================================================*/
/* Index: Index_name                                            */
/*==============================================================*/
create unique index Index_name on wc_options
(
   name
);

/*==============================================================*/
/* Table: wc_sub                                                */
/*==============================================================*/
create table wc_sub
(
   id                   bigint(20) not null auto_increment comment '订阅ID',
   user_id              bigint(20) not null comment '用户ID',
   st_id                bigint(20) not null comment '模板ID',
   count                int comment '订阅次数',
   enabled              enum('0','1') comment '是否启用',
   data                 json comment '订阅数据',
   primary key (id)
)
engine = InnoDB;

alter table wc_sub comment '订阅表';

/*==============================================================*/
/* Index: Index_user_sub                                        */
/*==============================================================*/
create unique index Index_user_sub on wc_sub
(
   user_id,
   st_id
);

/*==============================================================*/
/* Index: Index_enabled                                         */
/*==============================================================*/
create index Index_enabled on wc_sub
(
   enabled
);

/*==============================================================*/
/* Table: wc_sub_template                                       */
/*==============================================================*/
create table wc_sub_template
(
   id                   bigint(20) not null auto_increment comment '模板ID',
   code                 varchar(255) comment '模板代码',
   client               enum('wx','qq') comment '客户端',
   description          varchar(200) comment '模板描述',
   type                 int comment '模板类型',
   primary key (id)
)
engine = InnoDB;

alter table wc_sub_template comment '订阅模板表';

/*==============================================================*/
/* Index: Index_client                                          */
/*==============================================================*/
create index Index_client on wc_sub_template
(
   client
);

/*==============================================================*/
/* Table: wc_tag_taxonomy                                       */
/*==============================================================*/
create table wc_tag_taxonomy
(
   id                   bigint(20) not null auto_increment comment '标记ID',
   tag_id               bigint(20) comment '标签ID',
   taxonomy             varchar(20) comment '标记',
   primary key (id)
)
engine = InnoDB;

alter table wc_tag_taxonomy comment '标签标记表';

/*==============================================================*/
/* Index: Index_tag_taxonomy                                    */
/*==============================================================*/
create index Index_tag_taxonomy on wc_tag_taxonomy
(
   taxonomy
);

/*==============================================================*/
/* Table: wc_tags                                               */
/*==============================================================*/
create table wc_tags
(
   id                   bigint(20) not null auto_increment,
   name                 varchar(20),
   mark                 varchar(10) not null,
   primary key (id)
)
engine = InnoDB;

alter table wc_tags comment '标签|分类表';

/*==============================================================*/
/* Index: Index_tag_mark                                        */
/*==============================================================*/
create unique index Index_tag_mark on wc_tags
(
   mark
);

/*==============================================================*/
/* Table: wc_users                                              */
/*==============================================================*/
create table wc_users
(
   id                   bigint(20) not null auto_increment comment '用户ID',
   stu_id               varchar(20) not null comment '学生ID',
   stu_pass             text comment '学生密码',
   wx_id                varchar(20) not null comment '微信ID',
   qq_id                varchar(20) not null comment 'QQID',
   primary key (id)
)
engine = InnoDB;

alter table wc_users comment '用户账号表';

/*==============================================================*/
/* Index: Index_stu_id                                          */
/*==============================================================*/
create unique index Index_stu_id on wc_users
(
   stu_id
);

/*==============================================================*/
/* Index: Index_wx_id                                           */
/*==============================================================*/
create unique index Index_wx_id on wc_users
(
   wx_id
);

/*==============================================================*/
/* Index: Index_qq_id                                           */
/*==============================================================*/
create unique index Index_qq_id on wc_users
(
   qq_id
);

alter table wc_info_tag_relationship add constraint FK_ICT_REF_COLLEGE foreign key (college_id)
      references wc_colleges (id) on delete restrict on update restrict;

alter table wc_info_tag_relationship add constraint FK_ICT_REF_INFO foreign key (info_id)
      references wc_info (id) on delete restrict on update restrict;

alter table wc_info_tag_relationship add constraint FK_Reference_4 foreign key (tag_id)
      references wc_tags (id) on delete restrict on update restrict;

alter table wc_sub add constraint FK_Reference_3 foreign key (st_id)
      references wc_sub_template (id) on delete restrict on update restrict;

alter table wc_sub add constraint FK_SU_REF_USER foreign key (user_id)
      references wc_users (id) on delete restrict on update restrict;



# FOOTER
