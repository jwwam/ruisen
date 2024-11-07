
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
    +优先级（紧急、一般）【level】
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

系统替换svg模板https://www.iconfont.cn/
```
<svg t="1730975355744" class="mr-1 h-5 w-6 text-yellow-500" viewBox="0 0 1024 1024" version="1.1"
						   xmlns="http://www.w3.org/2000/svg" p-id="4273" width="128" height="128">
<path d="M319.968 960c-5.92 0-11.904-1.664-17.184-4.992-12.672-8.064-18.016-23.84-12.896-37.952L402.304 608 160 608c-12.928 0-24.608-7.776-29.568-19.744s-2.208-25.728 6.944-34.88l480-480c12.512-12.512 32.736-12.512 45.248 0s12.512 32.736 0 45.248L237.248 544 448 544c10.432 0 20.224 5.088 26.208 13.664 6.016 8.544 7.424 19.456 3.872 29.28l-78.72 216.448L786.752 416 576 416c-17.696 0-32-14.336-32-32s14.304-32 32-32l288 0c12.928 0 24.64 7.808 29.568 19.744 4.96 11.968 2.208 25.728-6.944 34.88l-544 544C336.448 956.8 328.224 960 319.968 960z" fill="#d81e06" p-id="4274">
</path>
</svg>
```

新增发起工单，提交人没拉出来
新增发起工单，处理人/抄送人显示id【新注册用户出现bug】，处理人/抄送人不能选自己

字典要加状态3-已终止
终止后列表操作不显示【已终止】按钮
【处理中】状态的工单不能被终止
我的发起没有过滤本人数据
客户名称、合作伙伴没数据，空值应该显示“-”
登录页面去掉手机号登录和注册找好，只保留账号登录
终止的数据保留给他，不能再显示终止按钮
rsmanage-ui/src/api/rs/work.ts增加2个接口查看客户列表，查看合作伙伴列表
后端客户和合作伙伴新增两个列表接口，不需要权限


增加角色给接口赋权限功能【避免其他组件请求接口没有权限】