$(document).ready(function(){
	$("#formCreateError").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"name": {
          		required: true,
        	},
        	"device": {
          		required: true,
        	},
      	},
      	messages: {
			"name": {
          		required: "Vui lòng nhập lỗi.",
        	},
        	"device": {
          		required: "Vui lòng chọn thiết bị.",
        	},
      	}
    });
	$(document).on('submit', '#formCreateError', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		
		$.ajax({
            url: getContextPath() + '/commonErrors/save',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				name: $('#name').val(),
				device: $('#device').val(),
			}),
            success: function(result){
            	let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					
					toastr.success(data.messages);
					$('#modal_creatError').modal('hide');
					$('#formCreateError')[0].reset();
					setTimeout(function() {
					   window.location.replace("/commonErrors");
					}, 700);
				}
				if(data.status === 'error'){
					toastr.error(data.messages);
				}
            }
	        });
		
		
	});
	
	$(document).on('click', '.btn-delete', function(){
		let id = $(this).attr('data-id');
		Swal.fire({
			title: "Bạn có chắc không?",
            text: "Bản ghi sẽ được xóa khỏi kho dữ liệu!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Đồng ý",
            cancelButtonText: "Không"
		}).then((data) => {
		  if (data.isConfirmed) {
			 $.ajax({
                url: getContextPath() + '/commonErrors/delete',
                type: 'POST',
				contentType: "application/json",
                data: JSON.stringify({
					id: id
				}),
                success: function(result){
                	let data = jQuery.parseJSON(result);
					if(data.status === 'success'){
						
						toastr.success(data.messages);
						setTimeout(function() {
						   window.location.replace("/commonErrors");
						}, 700);
					}
					if(data.status === 'error'){
						toastr.error(data.messages);
					}
                }
            });
		  }
		})

	})
	
	$(document).on('click', '.btn-edit', function(){
		let id = $(this).attr('data-id');
		$.ajax({
            url: getContextPath() + '/commonErrors/edit',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: id
			}),
            success: function(result){
            	$('#device_edit').val(result.device.id);
				$('#name_edit').val(result.name);
				$('#id_edit').val(result.id);
            	$('#modal_creatError2').modal('show');
            }
        });

	})
	
	$("#formUpdateError").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"name_edit": {
          		required: true,
        	},
        	"device_edit": {
          		required: true,
        	},
      	},
      	messages: {
			"name_edit": {
          		required: "Vui lòng nhập lỗi.",
        	},
        	"device_edit": {
          		required: "Vui lòng chọn thiết bị.",
        	},
      	}
    });
	$(document).on('submit', '#formUpdateError', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		
		$.ajax({
            url: getContextPath() + '/commonErrors/update',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: $('#id_edit').val(),
				name: $('#name_edit').val(),
				device: $('#device_edit').val(),
			}),
            success: function(result){
            	let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					toastr.success(data.messages);
					$('#modal_creatError2').modal('hide');
					$('#formUpdateError')[0].reset();
					setTimeout(function() {
					   window.location.replace("/commonErrors");
					}, 700);
				}
				if(data.status === 'error'){
					toastr.error(data.messages);
				}
            }
	        });
		
		
	});
})