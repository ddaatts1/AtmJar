$(document).ready(function(){
	$('#listItemErr').hide();
	$("#formCreateInventory").validate({
		onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"name_create": {
          		required: true,
        	},
    	},
    	messages: {
			"name_create": {
          		required: "Vui lòng nhập tên kho.",
        	},
    	}
	});
	
	$(document).on('submit', '#formCreateInventory', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		let check = true;
		$('#listItemErr').show();
		if($('#listItem').val() == ""){
			$('#listItemErr').show();
			$('#listItemErr').html("Vui lòng chỉ định cán bộ quản lý kho.");
			check = false;
		}
		if(check){
			$.ajax({
	            url: getContextPath() + '/inventories/save',
	            type: 'POST',
				contentType: "application/json",
	            data: JSON.stringify({
					name: $('#name_create').val(),
					regionId: $('#regionId_create').val(),
					address: $('#address_create').val(),
					userIds: $('#listItem').val()
				}),
				success: function(result) {
					console.log(result);
					if(result.status === 'success'){
						toastr.success(result.messages);
						$('#modal_create_inventory').modal('hide');
						$('#formCreateInventory')[0].reset();
					}
					if(result.status === 'error'){
						toastr.error(result.messages);
					}
					window.setTimeout(function(){location.reload()},1000)
				}
            }); 
        }
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
                url: getContextPath() + '/inventories/delete',
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
	
	$('#listItemErr_update').hide();
	$("#formUpdateInventory").validate({
		onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"name_update": {
          		required: true,
        	},
    	},
    	messages: {
			"name_update": {
          		required: "Vui lòng nhập tên kho.",
        	},
    	}
	});
	
	$(document).on('click', '.btn-edit', function(){
		let id = $(this).attr('data-id');
		getInventory(id);
//		filterUser($('#received_inventory').val());
	});
	
	function getInventory(id) {
		$.ajax({
			type: "GET",
			contentType: "application/json",
			url: getContextPath() + "/inventories/getOne",
            data: {
				id: id,
			},
			success: function(result) {
				console.log(result)
				$('#id_update').val(id);
				$('#name_update').val(result.name);
				$('#regionId_update').val(result.regionId);
				$('#address_update').val(result.address);
				$('#listItem_update').val(result.userIds);
				$('[name=listItem_update]').bootstrapDualListbox('refresh', true);
				
				$('#modal_update_inventory').modal('show');
			}
		});
	}
	
	$(document).on('submit', '#formUpdateInventory', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		let check = true;
		$('#listItemErr_update').show();
		if($('#listItem_update').val() == ""){
			$('#listItemErr_update').show();
			$('#listItemErr_update').html("Vui lòng chỉ định cán bộ quản lý kho.");
			check = false;
		}
		if(check){
			$.ajax({
	            url: getContextPath() + '/inventories/update',
	            type: 'POST',
				contentType: "application/json",
	            data: JSON.stringify({
					id: $('#id_update').val(),
					name: $('#name_update').val(),
					regionId: $('#regionId_update').val(),
					address: $('#address_update').val(),
					userIds: $('#listItem_update').val()
				}),
				success: function(result) {
					console.log(result);
					if(result.status === 'success'){
						toastr.success(result.messages);
						$('#modal_update_inventory').modal('hide');
						$('#formUpdateInventory')[0].reset();
					}
					if(result.status === 'error'){
						toastr.error(result.messages);
					}
					window.setTimeout(function(){location.reload()},1000)
				}
            }); 
        }
	});
});