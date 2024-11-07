
[参考地址](https://www.yuque.com/pig4cloud/pig/yhixg0ol9cp4qbb8)

# 启动顺序
1. pig-register/PigNacosApplication.java 
2. pig-auth/PigAuthApplication.java   
3. pig-upms-biz/PigAdminApplication
4. pig-gateway/PigGatewayApplication.java

/Users/miaomiaomiao/myproject/rsmanage/rsmanage/rsmanage-customer/rsmanage-customer-biz/src/main/java/com/ruisen/rsmanage/customer
/Users/miaomiaomiao/myproject/rsmanage/rsmanage-ui

# 分成比例mock数据:revenueShares
```json
[
    {
        "share_id": "",
        "partner_id": -176287742,
        "name": "分成比例套餐1",
        "share": 1,
        "description": "分成比例套餐1",
        "valid_days": 90,
        "is_active": false,
        "sort_order": 1
    },
    {
        "share_id": "",
        "partner_id": -176287742,
        "name": "分成比例套餐2",
        "share": 2,
        "description": "分成比例套餐2",
        "valid_days": 90,
        "is_active": true,
        "sort_order": 2
    }
]
```


# work工单mock模板


20241101需求：
客户信息【财务人员、财务联系人取一个】
1.分成有效天数改成有效月份，自己填写，再加一个无限期进去✅
2.分成比例，简化为两个字段，上游分成比例：，分成比例：✅
3.下拉带搜索功能
4.gam去掉后面3个字段
5.工单：
    工单名称（标题）：提交者+工单分类
    +工单分类（下拉+自定义）：
        数据缺失
        日报管理
        站点审核
        新通道邀请
    工单状态不可修改，提交时默认【待处理】
    处理人改为只选一个人[待办事项]
    +抄送人(可选多个)【只能查看】
    +下拉选择客户（单）
    +下拉合作伙伴（单）
    +截止日期
    +优先级（紧急、一般）
    首页，用户登录后显示待办事项（工单）【倒序截止日期+优先级】

6.流水记录，Excel
7.合同，+下载功能
8.报表

阿里云serverless+docker部署
阿里云数据库
文件云存储

文件名称
工单内容
工单提交，文件删除