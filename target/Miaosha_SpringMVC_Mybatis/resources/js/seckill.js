//存放主要逻辑js代码
//javascript也可以模块化，通过json的方式

var seckill= {
    //封装秒杀相关ajax的url
    URL: {
        basePath: function () {
            return '/';
        },
        now: function () {
            return seckill.URL.basePath() + 'seckill/time/now';
        },
        exposer: function (seckillId) {
            return seckill.URL.basePath() + 'seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return seckill.URL.basePath() + 'seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone))
            return true;
        else
            return false;
    },
    countdown:function (seckillId,nowTime,startTime,endTime) {
        //时间判断
        var seckillBox=$('#seckillBox');
        if(nowTime>endTime){
            //秒杀结束
            seckillBox.html('秒杀结束！');
        }else if(nowTime<startTime){
            //秒杀未开始。绑定一个计时事件
            var killTime=new Date(startTime+1000);
            seckillBox.countdown(killTime,function (event) {
                var format=event.strftime('秒杀倒计时%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown',function () {//倒计时完成后的回调事件
                //获取秒杀地址、控制显示逻辑，执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });
        }else{
            //秒杀开始
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化，登录交互，借助cookie完成
        init: function (params) {
            var killPhoneInCookie = $.cookie('killPhone');
            //验证cookie中的手机号，如果不正确，显示弹出层
            if (!seckill.validatePhone(killPhoneInCookie)) {
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                })
                //弹出层中提交按钮的响应事件
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killphoneKey').val();//弹出层中所填的手机号
                    console.log('inputPhone=' + inputPhone);//TODO
                    if (seckill.validatePhone(inputPhone)) {
                        //将这个新填的手机号填入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面，这样就会重新走上面的逻辑，即验证cookie中的手机号
                        window.location.reload();
                    } else {
                        $('#killphoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(),{},function (result) {
                if(result&&result['success']){
                    var nowTime=result['data'];
                    //时间判断，计时交互
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                }else{
                    console.log(result);
                }
            })
        }
    },
    //执行秒杀
    handleSeckill:function (seckillId,node) {
        //获取秒杀地址
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        console.log('exposerUrl=' + seckill.URL.exposer(seckillId));//TODO
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回调函数中执行交互流程
            if(result&&result['success']){
                var exposer=result['data'];
                if(exposer['exposed']){
                    var md5=exposer['md5'];
                    var killUrl=seckill.URL.execution(seckillId,md5);
                    console.log('killUrl='+killUrl);//TODO
                    //one表示只能点击一次
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求：先禁用按钮再发送秒杀请求
                        $(this).addClass('disabled');
                        $.post(killUrl,{},function (result) {
                            console.log('result='+result['success']);//TODO
                            if(result&&result['success']){
                                var killResult=result['data'];
                                var state=killResult['state'];
                                var stateInfo=killResult['stateInfo'];
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                }else{
                    // 未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    // 重新计算计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }
            }else{
                console.log('result=' + result);//TODO
            }
        })
    }
}
