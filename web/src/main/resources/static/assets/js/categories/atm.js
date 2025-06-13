$(document).ready(function(){
		localStorage.clear();
	$('#btn-export').on('click', function() {
		Swal.fire({
			title: "Bạn có chắc không?",
			text: "danh sách ATM sẽ được lưu về máy của bạn dưới dạng file excel!",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "Đồng ý",
			cancelButtonText: "Không"
		}).then((data) => {
			if (data.isConfirmed) {
				$.ajax({
					url: getContextPath() + "/atm-region/searchExcle",
					method: "POST",
					contentType: "application/json",
					xhrFields: {
						responseType: 'blob'
					},
					data: JSON.stringify({
						serial: $('#serial_number').val(),
						address: $('#address_search').val(),
						series: $('#series option:selected').val(),
						region: $('#region option:selected').val(),
						department:$('#department option:selected').val(),
					}),
					success: function(result) {
					if(result.size>3800){
						console.log(result.size);
						
						var blob = new Blob([result], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' });
						var URL = window.URL || window.webkitURL;
						var downloadUrl = URL.createObjectURL(blob);
						
						var fileLink = document.createElement('a');
						fileLink.href = downloadUrl;
						
						//Thành thêm export excel theo tên file
						var d = new Date();

						var month = d.getMonth()+1;
						var day = d.getDate();
						var year = d.getFullYear().toString().substr(-2);
						
						var time = (day<10 ? '0' : '') + day + (month<10 ? '0' : '') + month + year;
					
						fileLink.download = 'ThongkeATM_' + time;
						
						fileLink.click();
						toastr.success("Xuất báo cáo thành công!");
					}
					else{
						toastr.error("Không có dữ liệu ATM");
					}
						
					}
				});
			}
		})
	})
	
	$("#formCreateAtm").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"serial_number_create": {
          		required: true,
        	},
//        	"address_create": {
//          		required: true,
//        	},
        	"series_create": {
          		required: true,
        	},
			"region_create": {
          		required: true,
        	},
        	"department_create": {
          		required: true,
        	},
      	},
      	messages: {
			"serial_number_create": {
          		required: "Vui lòng nhập số serial.",
        	},
        	"series_create": {
          		required: "Vui lòng chọn dòng máy.",
        	},
        	"region_create": {
          		required: "Vui lòng chọn vùng.",
        	},
        	"department_create": {
          		required: "Vui lòng chọn đơn vị.",
        	},
      	}
    });
	$(document).on('submit', '#formCreateAtm', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		
		$.ajax({
            url: getContextPath() + '/atms/save',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				serial_number: $('#serial_number_create').val(),
				address: $('#address_create').val(),
				series_id: $('#series_create').val(),
				region_id: $('#region_create').val() === '' ? null : $('#region_create').val(),
				department_id: $('#department_create').val() === '' ? null : $('#department_create').val(),
			}),
            success: function(result){
            	let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					toastr.success(data.messages);
					$('#modal_createATM').modal('hide');
					$('#formCreateAtm')[0].reset();
					setTimeout(function() {
					   window.location.replace("/atms");
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
                url: getContextPath() + '/atms/delete',
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
						   window.location.replace("/atms");
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
            url: getContextPath() + '/atms/edit',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: id
			}),
            success: function(result){
            	$('#serial_number_edit').val(result.serialNumber);
				$('#address_edit').val(result.address);
				if (result.series != null) {
					$('#series_edit').val(result.series.id);
				}
				if (result.region != null) {
					$('#region_edit').val(result.region.id);
				}
				if (result.department != null) {
					$('#department_edit').val(result.department.id);
				}
				$('#id_edit').val(result.serialNumber);
				$('#status_edit').val(result.status);
            	$('#modal_createATM2').modal('show');
            }
        });

	})
	
	$("#formUpdateAtm").validate({
      	onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"serial_number_edit": {
          		required: true,
        	},
        	"series_edit": {
          		required: true,
        	},
        	"status_edit": {
          		required: true,
        	},
        	"region_edit": {
          		required: true,
        	},
        	"department_edit": {
          		required: true,
        	}
      	},
      	messages: {
			"serial_number_edit": {
          		required: "Vui lòng nhập số serial.",
        	},
        	"series_edit": {
          		required: "Vui lòng chọn dòng máy.",
        	},
        	"region_edit": {
          		required: "Vui lòng chọn vùng.",
        	},
        	"department_edit": {
          		required: "Vui lòng chọn đơn vị.",
        	}
      	}
    });
	$(document).on('submit', '#formUpdateAtm', function(e){
		e.preventDefault();
		var form = $(this);
      	form.valid();
		
		$.ajax({
            url: getContextPath() + '/atms/update',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				id: $('#id_edit').val(),
				serial_number: $('#serial_number_edit').val(),
				address: $('#address_edit').val(),
				series_id: $('#series_edit').val(),
				region_id: $('#region_edit').val() === '' ? null : $('#region_edit').val(),
				department_id: $('#department_edit').val() === '' ? null : $('#department_edit').val(),
			}),
            success: function(result){
            	let data = jQuery.parseJSON(result);
				if(data.status === 'success'){
					toastr.success(data.messages);
					$('#modal_createATM2').modal('hide');
					$('#formUpdateAtm')[0].reset();
					setTimeout(function() {
					   window.location.replace("/atms");
					}, 700);
					
				}
				if(data.status === 'error'){
					toastr.error(data.messages);
				}
            }
        });
		
		
	});
	
	$(document).on('click', '.btn_show_detail', function(){
		let serial = $(this).attr('data-serial-number');
		let url = getContextPath() + "/search?serial=" + serial;
		
		window.open(url, "_blank");
	})
	
	$(document).on('click', '#btnImport', function(){ 
		var regex = /^[a-zA-Z0-9\s_\\.\-:]+$/;  
			 
		 if (regex.test($("#inputFile").val().toLowerCase())) {    
			 if ($("#inputFile").val().toLowerCase().indexOf(".xlsx") >0||$("#inputFile").val().toLowerCase().indexOf(".xls") >0) {  
		var form_data = new FormData();
		var file_data = $('#inputFile').prop('files')[0];
		form_data.append('inputFile', file_data);
		$('#modal_import_atm_data').modal('toggle');
		document.getElementById("loader").style.display = "block";
		$.ajax({
      		type: 'POST',
        	url: getContextPath() + '/import-atm-data',
        	contentType: false,
	 		processData: false,
	    	data: form_data,
 			success: function(response) {
				document.getElementById("loader").style.display = "none";
				toastr.success(response);
				$("#inputFile").val('');
					window.setTimeout(function(){location.reload()},1000)
            },
            error: function(response) {
				document.getElementById("loader").style.display = "none";
				toastr.error("Lỗi format excle!");
				$("#inputFile").val('');
			}
        });
                } else{
		toastr.error("không đúng định dạng file .xlsx,.xls");
	//window.setTimeout(function(){location.reload()},2000)
          }
			
			
		}else{
			toastr.error("Chọn file import ");
			//window.setTimeout(function(){location.reload()},2000)
		}
      
        
	})
	
	function loadDataInregions() {
		$.ajax({
			url: getContextPath() + "/atm-region/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				regionId: $('#region').children("option:selected").val(),
				departmentId: $('#department').children("option:selected").val(),
	
			}),
			success: function(result) {
				$('#department').empty();
				let html = '';
				html += `
				<option value="">Tất cả</option>
				`;
				for (let i = 0; i < result.length; i++) {
	            let selected = '';
					if ((result[i]['id'] == $('#departmentParamAtm').val())) {
						selected = 'selected';
					}
					html += `
					<option value="`+ result[i]['id'] + `"`+ selected +`>
					`+ result[i]['name'] + `
					</option>
					`;
				}
	
				$('#department').append(html);
	
			}
		})
	}
	
	loadDataInregions()
	$('#region').on('change', function() {
		loadDataInregions()
	})
	
	$(document).on('click', '.btn-change-status', function() {
		var serial = $(this).attr('data-id');
		var currStatus = $(this).attr('data-status');
		var txt = '';
		if (currStatus == 1) {
			txt = 'Ngừng cung cấp dịch vụ trên máy ' + serial + '!'	
		}else {
			txt = 'Bắt đầu cung cấp dịch vụ trên máy ' + serial + '!'	
		}
		Swal.fire({
			title: "Bạn có chắc không?",
			text: txt,
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "Đồng ý",
			cancelButtonText: "Không"
		}).then((data) => {
			if (data.isConfirmed) {
				$.ajax({
					url: getContextPath() + "/atms/change-status",
					method: "GET",
					contentType: "application/json",
					data: {
						serialNumber: serial
					},
					success: function(result) {
						result = jQuery.parseJSON(result);
						if (result.status === 'success'){
							toastr.success(result.messages);
							window.setTimeout(function(){location.reload()}, 1500)
						}
						if(result.status === 'error') {
							toastr.error(result.messages);
							window.setTimeout(function(){location.reload()}, 1500)
						}
					}
				});
			}
		});
	})
})