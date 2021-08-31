//导航栏
(function () {
    function dhl(id, cName) {
        $$(id).on('click', toggle);
        function toggle() {
            $$(id).removeClass(cName);
            $$(this).addClass(cName);
        }
    }

    new dhl('#m-nav li a', 'active');
    new dhl("#b-nav li a", 'active');
})();

//播放器样式
(function () {
    $$('#play .kz-p').click(function () {
        $$(this).toggleClass('kz-play');
    });
    $$('#g_play .a-suo').click(function () {
        if ($$(this).hasClass('suo-click')) {
            $$(this).removeClass('suo-click');
            $$('#g_play').on('mouseleave', fn2);
        } else {
            $$(this).addClass('suo-click');
            $$('#g_play').un('mouseleave', fn2);
        }
    });
    $$('#g_play').on('mouseover', fn1).on('mouseleave', fn2);
    var flag = true;

    function fn1() {
        var h = $$(this).css('bottom');
        if (flag && h != '0px') {
            flag = false;
            $$(this).animate({bottom: 0}, 200, 'linear', ft);
        }
    }

    function fn2() {
        if (flag) {
            flag = false;
            $$(this).animate({bottom: -45}, 200, 'linear', ft);
        }
    }

    function ft() {
        flag = true;
    }
})();
//播放器进度条
(function () {
    var pl = $$('.tiao')[0].offsetLeft;
    var pw = $$('.tiao')[0].offsetWidth;
    //进度条点击
    $$('.tiao').on('click', getWidth);
    function getWidth(e) {
        var x = $$.getEvent(e).clientX;
        var w = Math.ceil((x - pl) / pw * 100);
        if (x >= pl && x <= (pl + pw)) {
            $$('.t-btn').css('left', x - pl + 'px');
            $$('.t-red').css('width', w + '%');
        }
    }

    var bx = null;
    var px = 0;
    $$('.t-btn').on('mousedown', getX);
    $$('#g_play').on('mousemove', setX);
    //$$('#g_play').on('mousemoup', clearX);
    document.onmouseup = clearX;
    function getX(e) {
        this.ondragstart = function () {
            return false;
        };
        bx = $$.getEvent(e).clientX;
        px = parseFloat($$(this).css('left'));
    }

    function setX(e) {
        if (bx) {
            var x = $$.getEvent(e).clientX;
            var w = Math.ceil((px + x - bx) / pw * 100);
            if (x >= pl && x <= (pl + pw)) {
                $$('.t-btn').css('left', px + x - bx + 'px');
                $$('.t-red').css('width', w + '%');
            }
        }
    }

    function clearX() {
        bx = null;
        px = 0;
    }

})();

//top按钮
(function () {
    window.onscroll = function () {
        //console.log(window.scrollY);
        var y = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
        if (y > 50) {
            $$('#g_back').show();
        } else {
            $$('#g_back').hide();
        }
    };
})();

//登录
(function () {
    $$('.m-login').on('mouseover', fnShow).on('mouseleave', fnHide);
    function fnShow() {
        $$('.login-inner').show();
    }

    function fnHide() {
        $$('.login-inner').hide();
    }
})();

//登录框
(function () {
    function showDl(e) {
        $$.preventDefault(e);
        $$('#dl-tp').show();
    }

    function hideDl() {
        $$('#dl-tp').hide();
        $$('.denglu').css({left: '50%', top: '150px'});
    }

    $$('#a-login').click(showDl);
    $$('.dl-close').click(hideDl);
})();

//登录框移动
(function () {
    $$('.dl-top').on('mousedown', get).on('mousemove', move);
    $$('#dl-tp').on('mouseup', clear);
    var x = 0;
    var y = 0;
    var flag = false;

    function get(e) {
        this.ondragstart = function () {
            return false;
        };
        flag = true;
        var event = $$.getEvent(e);
        x = event.x - ($$('.denglu')[0]['offsetLeft']-parseFloat($$('.denglu').css('marginLeft')));
        y = event.y - $$('.denglu')[0]['offsetTop'];
        //x = event.x - $$('.denglu')[0]['offsetLeft'];
        //y = event.y - $$('.denglu')[0]['offsetTop'];
        //console.log($$('.denglu')[0]['offsetLeft']-parseFloat($$('.denglu').css('marginLeft')), $$('.denglu')[0]['offsetTop']);
        //console.log(parseFloat($$('.denglu').css('left')), parseFloat($$('.denglu').css('top')));
    }

    function move(e) {
        if (flag) {
            var event = $$.getEvent(e);
            var nx = event.x - x;
            var ny = event.y - y;
            $$('.denglu').css({top: ny + 'px', left: nx + 'px'});
        }
    }

    function clear() {
        flag = false;
        x = 0;
        y = 0;
    }
})();