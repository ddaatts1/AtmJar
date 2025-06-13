$(document).ready(function(){
	$('#department_err').hide();
	$('#department_edit_err').hide();
	$("#formCreateDepartment").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
        	"name": {
          		required: true,
        	},
      	},
      	messages: {
        	"name": {
          		required: "Vui lòng nhập tên đơn vị chuyên trách.",
        	},
      	}
    });
	$(document).on('submit', '#formCreateDepartment', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		$.ajax({
			type: "POST",
			contentType: "application/json",
			url: getContextPath() + "/department/save",
			data: JSON.stringify({
				name: $('#name').val(),
				region: $('#region-create').children("option:selected").val(),
			}),
			cache: false,
			success: function(result) {
				let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					toastr.success(data.messages);
					$('#modal_createDepartment').modal('hide');
					$('#formCreateDepartment')[0].reset();
					
				}
				if(data.status === 'error'){
					toastr.error(data.messages);
				}
				window.setTimeout(function(){location.reload()},1000)
			},
			error: function(result) {
				toastr.error(result);
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
		}).then((result) => {
		  if (result.isConfirmed) {
			 $.ajax({
                url: getContextPath() + '/department/delete',
                type: 'POST',
				contentType: "application/json",
                data: JSON.stringify({
					id: id
				}),
                success: function(result){
                	let data = jQuery.parseJSON(result);
					if(data.status === 'success'){
						
						toastr.success(data.messages);
						window.setTimeout(function(){location.reload()},1000)
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
            url: getContextPath() + '/department/edit',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: id
			}),
            success: function(result){
            	$('#name_edit').val(result.name);
            	$('#id_edit').val(result.id);
            	if (result.regionId !== null) {
            		$('#region-edit').val(result.regionId);
            	} 
            	$('#modal_createDepartment2').modal('show');
            }
        });

	})
	
	$("#formUpdateDepartment").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
        	"name_edit": {
          		required: true,
        	},
      	},
      	messages: {
        	"name_edit": {
          		required: "Vui lòng nhập tên đơn vị chuyên trách.",
        	},
      	}
    });

	$(document).on('submit', '#formUpdateDepartment', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		$.ajax({
			type: "POST",
			contentType: "application/json",
			url: getContextPath() + "/department/update",
			data: JSON.stringify({
				name: $('#name_edit').val(),
				id: $('#id_edit').val(),
				region: $('#region-edit').children("option:selected").val(),
			}),
			cache: false,
			success: function(result) {
				let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					toastr.success(data.messages);
					$('#modal_createDepartment2').modal('hide');
					window.setTimeout(function(){location.reload()},1000)
				}
				if(data.status === 'error'){
					toastr.error(data.messages);
				}
			},
			error: function(err, result) {
				toastr.error(result);
			}
		});
	});
})