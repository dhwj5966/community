$(function() {
    $("#topBut").click(setTop);
    $("#wonderfulBut").click(setWonderful);
    $("#deleteBut").click(setDelete);
});

function like(btn,entityType,entityId,targetUserId,postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"targetUserId":targetUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code==0) {
                $(btn).children("b").text(data.likeStatus == 1? "已点赞" : "赞同");
                $(btn).children("i").text(data.count);
                var btnid = $(btn).attr("id");
                if (data.likeStatus == 1) {
                    if (btnid == "btn1") {
                        $(btn).attr("class", "btn btn-success");
                    }
                    if (btnid == "btn2") {
                        $(btn).attr("class", "btn btn-success btn-sm");
                    }
                }
                if (data.likeStatus == 0) {
                    if (btnid == "btn1") {
                        $(btn).attr("class", "btn btn-primary");
                    }
                    if (btnid == "btn2") {
                        $(btn).attr("class", "btn btn-info btn-sm");
                    }
                }
            } else {
                alert(data.msg);
            }
        }
    )
};

function setTop() {
    $.post(
        CONTEXT_PATH + "/discussPost/top",
        {"id" : $("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#topBut").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discussPost/wonderful",
        {"id" : $("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#wonderfulBut").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

function setDelete() {
    $.post(
        CONTEXT_PATH + "/discussPost/delete",
        {"id" : $("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}





