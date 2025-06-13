$(document).ready(function(){
	$('#series_err').hide();
	$('#series_edit_err').hide();
	$("#formCreateAccessories").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"devices": {
          		required: true,
        	},
        	"name_accessory": {
          		required: true,
        	},
      	},
      	messages: {
			"devices": {
          		required: "Vui lòng chọn thiết bị.",
        	},
        	"name_accessory": {
          		required: "Vui lòng nhập tên linh kiện.",
        	},
      	}
    });
	$(document).on('submit', '#formCreateAccessories', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		let check = true;
		let series = $('#series').val();
		if	(series == ""){
			check = false;
			$("#series_err").show();
			$("#series_err").html("Vui lòng chọn dòng máy phù hợp.");
		} else {
			$('#series_err').hide();
		}
		if(check){
			$.ajax({
	            url: getContextPath() + '/accessories/save',
	            type: 'POST',
				contentType: "application/json",
	            data: JSON.stringify({
					device: $('#devices').val(),
					name: $('#name_accessory').val(),
					series: series
				}),
	            success: function(result){
	            	let data = jQuery.parseJSON(result);
					if(data.status === 'success'){
						toastr.success(data.messages);
						$('#modal_createAccessory').modal('hide');
						$('#formCreateAccessories')[0].reset();
						$('[name=series]').bootstrapDualListbox('refresh', true);
						setTimeout(function() {
						   window.location.replace("/accessories");
						}, 700);
					}
					if(data.status === 'error'){
						toastr.error(data.messages);
					}
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
		}).then((check) => {
		  if (check.isConfirmed) {
			 $.ajax({
                url: getContextPath() + '/accessories/delete',
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
						   window.location.replace("/accessories");
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
            url: getContextPath() + '/accesories/edit',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: id
			}),
            success: function(result){
            	$('#devices_edit').val(result.errorDevice.id);
				$('#name_accessory_edit').val(result.name);
				$('#id_edit').val(result.id);
				
				let series = result.series;
				let arr = [];
				if (series.length > 0){
					for(var i = 0; i < series.length; i++){
						arr[i] = series[i]['id'];
					}
				}
				$('#series_edit').val(arr);
				$('[name=series_edit]').bootstrapDualListbox('refresh', true);
				
            	$('#modal_createAccessory2').modal('show');
            }
        });

	})
	
	$("#formUpdateAccessories").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"devices_edit": {
          		required: true,
        	},
        	"name_accessory_edit": {
          		required: true,
        	},
      	},
      	messages: {
			"devices_edit": {
          		required: "Vui lòng chọn thiết bị.",
        	},
        	"name_accessory_edit": {
          		required: "Vui lòng nhập tên linh kiện.",
        	},
      	}
    });
	$(document).on('submit', '#formUpdateAccessories', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		let check = true;
		let series = $('#series_edit').val();
		if	(series == ""){
			check = false;
			$("#series_edit_err").show();
			$("#series_edit_err").html("Vui lòng chọn dòng máy phù hợp.");
		} else {
			$('#series_edit_err').hide();
		}
		if(check){
			$.ajax({
	            url: getContextPath() + '/accessories/update',
	            type: 'POST',
				contentType: "application/json",
	            data: JSON.stringify({
					id: $('#id_edit').val(),
					device: $('#devices_edit').val(),
					name: $('#name_accessory_edit').val(),
					series: series
				}),
	            success: function(result){
	            	let data = jQuery.parseJSON(result);
					if(data.status === 'success'){
						toastr.success(data.messages);
						$('#modal_createAccessory2').modal('hide');
						$('#formUpdateAccessories')[0].reset();
						setTimeout(function() {
						   window.location.replace("/accessories");
						}, 700);
					}
					if(data.status === 'error'){
						toastr.error(data.messages);
					}
	            }
	        });
		
		}
	});
	
	$(document).on('click', '#btn-export', function() {
		Swal.fire({
			title: "Bạn có chắc không?",
	        text: "Báo cáo sẽ được lưu về máy của bạn dưới dạng file excel!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Đồng ý",
            cancelButtonText: "Không"
		}).then((check) => {
		  if (check.isConfirmed) {
			 $.ajax({
                url: getContextPath() + '/accessories/export',
                type: 'POST',
				contentType: "application/json",
				xhrFields:{
	                responseType: 'blob'
	            },
                success: function(result){
                	var blob = new Blob([result], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'});
					var URL = window.URL || window.webkitURL;
			        var downloadUrl = URL.createObjectURL(blob);
			        
			        var fileLink = document.createElement('a');
						fileLink.href = downloadUrl;
					fileLink.download = 'Danh-sach-linh-kien';
					fileLink.click();
			        
			        toastr.success("Xuất dữ liệu thành công thành công!");
                }
            });
		  }
		})
	})
	
	$(document).on('click', '#btnImport', function(){ 
		var regex = /^[a-zA-Z0-9\s_\\.\-:]+$/;  
			 
		 if (regex.test($("#inputFile").val().toLowerCase())) {
			 if ($("#inputFile").val().toLowerCase().indexOf(".xlsx") >0||$("#inputFile").val().toLowerCase().indexOf(".xls") >0) {  
				var form_data = new FormData();
				var file_data = $('#inputFile').prop('files')[0];
				form_data.append('file', file_data);
				$('#modal_import_data').modal('toggle');
				document.getElementById("loader").style.display = "block";
				$.ajax({
		      		type: 'POST',
		        	url: getContextPath() + '/accessories/import',
		        	contentType: false,
			 		processData: false,
			    	data: form_data,
		 			success: function(result) {
						document.getElementById("loader").style.display = "none";
						toastr.success(result);
						$("#inputFile").val('');
						window.setTimeout(function(){location.reload()},1000)
		            },
		            error: function(result) {
						document.getElementById("loader").style.display = "none";
						toastr.error(result.responseText);
						$("#inputFile").val('');
						window.setTimeout(function(){location.reload()},2000)
					}
		        });
            } else{
				toastr.error("không đúng định dạng file .xlsx,.xls");
      		}
		}else{
			toastr.error("Chọn file import ");
			//window.setTimeout(function(){location.reload()},2000)
		}
	})
})