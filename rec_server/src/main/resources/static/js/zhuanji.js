(function() {
    $$('#flag_crl').on('click', show)

    function show() {
        if ($$("#flag_crl span").html() == '展开') {
            $$("#flag_crl span").html("收起");
            $$('#flag_crl i').removeClass('zk').addClass('sq');
            $$("#jies_dot").addClass('f-hide');
            $$("#jies_more").removeClass('f-hide');
        } else {
            $$("#flag_crl span").html("展开");
            $$('#flag_crl i').removeClass('sq').addClass('zk');
            $$("#jies_dot").removeClass('f-hide');
            $$("#jies_more").addClass('f-hide');
        }
    }
})();
