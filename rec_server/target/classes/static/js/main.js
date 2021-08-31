//小轮播图
(function() {
    var i = 0;
    var j = 1;
    var a = -656 + 'px';
    var b = -11 + 'px';
    var c = 634 + 'px';
    var flag = true;
    $$("#die_banner .a-lt").on('click', lbt);
    $$("#die_banner .a-rt").on('click', rbt);

    function lbt(e) {
        $$.preventDefault(e);
        if (flag) {
            flag = false;
            $$('#die_banner .banners ul').eq(j).css('left', a);
            $$('#die_banner .banners ul').eq(i).animate({ left: c }, 1000, 'linear');
            $$('#die_banner .banners ul').eq(j).animate({ left: b }, 1000, 'linear', tf);
            kzq('+');
        }
    }

    function rbt(e) {
        $$.preventDefault(e);
        if (flag) {
            flag = false;
            $$('#die_banner .banners ul').eq(j).css('left', c);
            $$('#die_banner .banners ul').eq(i).animate({ left: a }, 1000, 'linear');
            $$('#die_banner .banners ul').eq(j).animate({ left: b }, 1000, 'linear', tf);
            kzq('-');
        }
    }

    function tf() {
        flag = true;
    }

    function kzq(fh) {
        if (fh == '-') {
            i--;
            j--;
            if (j < 0) {
                j = 1;
            }
            if (i < 0) {
                i = 1;
            }
        } else if (fh == '+') {
            i++;
            j++;
            if (j > 1) {
                j = 0;
            }
            if (i > 1) {
                i = 0;
            }
        }
    }
})();

//主页大轮播图
(function() {
    var dimg = ["img/banner/bg_01.jpg", "img/banner/bg_02.jpg", "img/banner/bg_03.jpg", "img/banner/bg_04.jpg", "img/banner/bg_05.jpg", "img/banner/bg_06.jpg", "img/banner/bg_07.jpg", "img/banner/bg_08.jpg"];
    var iimg = ["img/banner/banner_01.jpg", "img/banner/banner_02.jpg", "img/banner/banner_03.jpg", "img/banner/banner_04.jpg", "img/banner/banner_05.jpg", "img/banner/banner_06.jpg", "img/banner/banner_07.jpg", "img/banner/banner_08.jpg"];
    var i = 0;
    var flag = true;
    var tt = "";
    bd();
    //初始化绑定
    function bd() {
        xd(i);
        setSit();
        //自动轮播控制
        $$('#m_banner').on('mouseout', setSit);
        $$('#m_banner').on('mouseover', clearSit);
        //左右方向控制
        $$('#m_banner .b-l').on('click', bl);
        $$('#m_banner .b-r').on('click', br);
        //小点控制
        $$('#m_banner .b-tods a').on('click', xiaoD);
    }

    //控制小点样式
    function xd(i) {
        $$('#m_banner .b-tods a').removeClass('active');
        $$('#m_banner .b-tods a').eq(i).addClass('active');
    }

    //向左
    function bl() {
        kzq('-');
        change();
    }

    //向右
    function br() {
        kzq("+");
        change();
    }

    //小点点击
    function xiaoD(e) {
        i = $$(this).index();
        change();
    }

    //改变
    function change() {
        $$('#m_banner').css('backgroundImage', 'url(' + dimg[i] + ')');
        $$('#m_banner img').attr('src', iimg[i]);
        xd(i);
    }

    //控制展图片示序号
    function kzq(fh) {
        if ('-' == fh) {
            i--;
            if (i < 0) {
                i = 7;
            }
        } else if ('+' == fh) {
            i++;
            if (i > 7) {
                i = 0;
            }
        }
    }

    //自动播放，背景透明后执行函数
    function tf() {
        kzq("+");
        change();
        $$('#m_banner img').animate({ opacity: 1 }, 200, 'linear');
        flag = true;
    }

    //开启定时器
    function setSit() {
        tt = setInterval(function() {
            if (flag) {
                flag = false;
                $$('#m_banner img').animate({ opacity: 0 }, 800, 'linear', tf);
            }
        }, 6000);
    }

    //关闭定时器
    function clearSit() {
        clearInterval(tt);
    }
})();

(function() {
    function showDl(e) {
        $$.preventDefault(e);
        $$('#dl-tp').show();
    }
    $$('#user_login').click(showDl);
})();
