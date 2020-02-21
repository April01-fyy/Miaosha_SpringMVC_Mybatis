<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <%@include file="common/head.jsp"%>
    <title>秒杀详情页</title>
</head>
<body>
    <input type="hidden" id="basePath" value="${basePath}"/>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${seckill.name}</h1>
            </div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <!--显示time图标-->
                    <span class="glyphicon glyphicon-time"></span>
                    <!--展示倒计时-->
                    <span class="glyphicon" id="seckillBox"></span>
                </h2>
            </div>
        </div>

        <!--登录弹出层，输入电话-->
        <div id="killPhoneModal" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 class="modal-title text-center">
                            <span class="glyphicon glyphicon-phone">秒杀电话：</span>
                        </h3>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-xs-8 col-xs-offset-2">
                                <input type="text" name="killphone" id="killphoneKey"
                                placeholder="填手机号" class="form-control">
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <span id="killphoneMessage" class="glyphicon"></span>
                        <button type="button" id="killPhoneBtn" class="btn btn-success">
                            <span class="glyphicon glyphicon-phone"></span> 提交
                        </button>
                    </div>
                </div>

            </div>

        </div>

    </div>



<!-- jQuery (Bootstrap 的所有 JavaScript 插件都依赖 jQuery，所以必须放在前边) -->
<script src="https://cdn.jsdelivr.net/npm/jquery@1.12.4/dist/jquery.min.js"></script>
<!-- 加载 Bootstrap 的所有 JavaScript 插件。你也可以根据需要只加载单个插件。 -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>
    <!-- jQuery cookie操作插件 -->
    <script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <!-- jQery countDonw倒计时插件  -->
    <script src="//cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
    <!-- 开始编写交互逻辑 -->
    <script src="${basePath}/resources/js/seckill.js"  type="text/javascript"></script>
    <script type="text/javascript">
        $(function(){
            //使用EL表达式传入参数
            seckill.detail.init({
                seckillId : ${seckill.seckillId},
                startTime : ${seckill.startTime.time},//毫秒
                endTime : ${seckill.endTime.time}
            });
        });
    </script>
</body>
</html>