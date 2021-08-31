function sendSubmit() {

    var uname = document.getElementById("name").value
    var sex = document.getElementById("sex").value
    var phoneNumber = document.getElementById("mobile").value
    var password = document.getElementById("psd").value
    if(uname == "" || sex == "" || phoneNumber == "" || phoneNumber == "" || password == "" ){
        alert("存在信息没填写！")
    }
    else{
        $.ajax({
            url: '/regist',
            type: "POST",
            data:{
                uname: uname,
                sex: sex="男"? 1: (sex ="女"? 0: 2),
                phoneNumber: phoneNumber,
                password: password
            },

            success: function (){
                alert("注册成功!")
            }
        })
    }

}


function sendToLogin() {

    var phoneNumber = document.getElementById("mobile").value
    var password = document.getElementById("psd").value
    console.log()
    $.ajax({
        url: '/login',
        type: "POST",
        data: {
            phoneNumber: phoneNumber,
            password: password
        },

        success: function (){
            // alert("登录成功！")
        }


    })
}