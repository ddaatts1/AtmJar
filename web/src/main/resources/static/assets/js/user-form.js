$(document).ready(function(){
	$("#userForm").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"username": {
          		required: true,
        	},
        	"password": {
          		required: true,
        	},
        	"email": {
          		required: true,
        	},
      	},
      	messages: {
			"username": {
          		required: "Vui lòng nhập tên đăng nhập.",
        	},
        	"password": {
          		required: "Vui lòng nhập mật khẩu.",
        	},
        	"email": {
          		required: "Vui lòng nhập email.",
        	},
      	}
    });
    
    $('#btn-submit').on('click', function() {
		var form = $('#userForm');
      	form.valid();
      	let check = true;
      	if($('#listItem').val() == ""){
			$('#listItemErr').show();
			$('#listItemErr').html("Vui lòng cấp quyền truy cập.");
			check = false;
		}
      	if(check){
			form.submit();
		}
	})
	
	$('#username').on('change', function() {
		var username = $('#username').val();
//		$()
		
		$('#username-error').show();
		$('#username-error').text('Tên đăng nhập đã tồn tại, vui lòng chọn tên khác.');
	})
});