(function (){
	$$('#flag_crl').on('click',show)
	function show(){
		$$('#flag_more').hasClass('f-hide')?$$('#flag_more').removeClass('f-hide'):$$('#flag_more').addClass('f-hide')
		if ($$("#flag_crl span").html() == '展开') {
			$$("#flag_crl span").html("收起");
			$$('#flag_crl i').removeClass('zk').addClass('sq');
		} else {
			$$("#flag_crl span").html("展开");
			$$('#flag_crl i').removeClass('sq').addClass('zk');
		}
	}
})();