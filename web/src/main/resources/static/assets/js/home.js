$(document).ready(function(){
	$(document).on('click', '.btn_show_detail', function(){
		let serial = $(this).attr('data-serial-number');
		let url = getContextPath() + "/search?serial=" + serial;
		
		window.open(url, "_blank");
	})
})