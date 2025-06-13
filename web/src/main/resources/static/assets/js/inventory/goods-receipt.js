$(document).ready(function(){
	localStorage.clear();
	genDataTable([]);
	getPartByType();
	
//	$('#listItemErr').hide();
	$('form input').keydown(function (e) {
	    if (e.keyCode == 13) {
	        e.preventDefault();
	        return false;
	    }
	});
})

// Filter part
$(document).on('change', '#part_type', function() {
	getPartByType();
});

function getPartByType() {
	$.ajax({
		url: getContextPath() + '/inventories/getByType',
		type: 'GET',
		contentType: "application/json",
		data: {
			inventoryId: $('#inventoryId').val(),
			partType: $('#part_type').val()
		},
		success: function(result) {
			console.log(result);
			$('#parts').empty();
			let html = '<option value="">------- Chọn part -------</option>';
			for (var item of result) {
				html += `<option value="`+ item.id + `">`+ item.name + `</option>`;
			}
			$('#parts').append(html);
		}
	});
}

// Add part modal nhập xuất
$(document).on('click', '#btn-add-part', function() {
	// table  #tblTrackingDetails 
	
	var quantityVal = $('#quantity').val();
	var partVal = $('#parts option:selected').val();
	
	// Check quantity
	if ((typeof quantityVal === 'undefined' || Number(quantityVal) > 0) && partVal !== '') {
		var arr = [];
		if (localStorage.getItem('parts') != null) {
			arr = JSON.parse(localStorage.getItem('parts'));
		}
		
		const part = {};
		part.partId = partVal;
		part.partName = $('#parts option:selected').text();
		part.partType = $('#part_type option:selected').val();
		part.partTypeStr = $('#part_type option:selected').text();
		part.quantity = $('#quantity').val();
		
		var index = -1;
		
		for (let i = 0; i < arr.length; i++) {
			if (part.partId == arr[i].partId) {
				part.quantity = Number(part.quantity) + Number(arr[i].quantity);
				index = i;
			}
		}
		
		if (index >= 0) {
			arr.splice(index, 1, part);
		}else {
			arr.push(part);
		}
		
		localStorage.setItem('parts', JSON.stringify(arr));
		genDataTable(arr);
		$('#quantity').val('');
	}
})

function genDataTable(arr) {
	let stt = 0;
	$('#tblTrackingDetails').dataTable({
		"lengthChange": true,
		"searching": true,
		"destroy": true,
		"data": arr,
      	"autoWidth": true,
      	"pageLength": 10,
		"columns":[
			{
				data: null,
				render: function(data){
					stt = stt + 1
					return stt;
				},
				class: 'text-center'
			},
			{
				data: null,
				render: function(data){
					let txt = data.partName;
					return txt;
				}
			},
			{
				data: null,
				render: function(data){
					let txt = data.partTypeStr;
					return txt;
				},
				class: 'text-center'
			},
			{
				data: null,
				render: function(data){
					let txt = data.quantity;
					return txt;
				},
				class: 'text-center'
			},
			{
				data: null,
				render: function(data){
					let txt = '';
					txt += '<a class="fas fa-trash-alt btn-delete-part" data-id="'+stt+'" style="color: red"></a>';
				
					return txt;
				},
				class: 'text-center'
			},
		]
	})
}

$(document).on('click', '.btn-delete-part', function() {
	let index = $(this).attr('data-id');
	var arr = JSON.parse(localStorage.getItem('parts'));
	
	// Xóa phần tử vừa chọn
	arr.splice(index-1, 1);
	
	//Lưu lại array và gen table
	localStorage.setItem('parts', JSON.stringify(arr));
	genDataTable(arr);
})

// export template nhập hàng
$(document).on('click', '.btn-export', function() {
	$.ajax({
        url: getContextPath() + '/inventory/export-template-goods-receipt',
        method: "GET",
		contentType: "application/json",
		xhrFields:{
            responseType: 'blob'
        },
        success: function(result) {
			var blob = new Blob([result], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'});
			var URL = window.URL || window.webkitURL;
	        var downloadUrl = URL.createObjectURL(blob);
	        
	        var fileLink = document.createElement('a');
				fileLink.href = downloadUrl;
				
			fileLink.download = 'Mau-file-nhap-hang';
			
			fileLink.click();
		}
	});
});

$(document).on('click', '#btnImport', function(){ 
	var regex = /^[a-zA-Z0-9\s_\\.\-:]+$/;  
		 
	if (regex.test($("#inputFile").val().toLowerCase())) {
		if ($("#inputFile").val().toLowerCase().indexOf(".xlsx") >0||$("#inputFile").val().toLowerCase().indexOf(".xls") >0) { 
			var form_data = new FormData();
			var file_data = $('#inputFile').prop('files')[0];
			form_data.append('inputFile', file_data);
			form_data.append('parts', localStorage.getItem('parts'));
			form_data.append('inventoryId', $('#inventoryId').val());
			$('#modal_import_atm_data').modal('toggle');
			document.getElementById("loader").style.display = "block";
			$.ajax({
		  		type: 'POST',
		    	url: getContextPath() + '/inventory/import-goods-receipt',
		    	contentType: false,
		 		processData: false,
		    	data: form_data,
				success: function(result) {
					console.log(result);
					document.getElementById("loader").style.display = "none";
					$("#inputFile").val('');
					$('#modal_import_atm_data').hide();
					localStorage.clear();
					genDataTable(result);
					localStorage.setItem('parts', JSON.stringify(result));				
					toastr.success("Tải dữ liệu thành công!");
		        },
		        error: function(response) {
					document.getElementById("loader").style.display = "none";
					toastr.error("Lỗi format excel!");
					$("#inputFile").val('');
				}
		    });
		}else {
			toastr.error("không đúng định dạng file .xlsx,.xls");
			//window.setTimeout(function(){location.reload()},2000)
	  	}
		
	}else {
		toastr.error("Chọn file import ");
		//window.setTimeout(function(){location.reload()},2000)
	}
})

$(document).on('submit', '#goods-receipt-form', function(e) {
	e.preventDefault();
	var form = $(this);
  	form.valid();
  	let check = true;
	var arr = [];
	if (localStorage.getItem('parts') === null || JSON.parse(localStorage.getItem('parts')).length <= 0) {
		check = false;
		toastr.error("Chưa có thông tin part nhập vào kho.");
	}else {
		arr = JSON.parse(localStorage.getItem('parts'));
	}
	if(check){
		Swal.fire({
			title: "Bạn có chắc không?",
//		    text: "!",
		    icon: "warning",
		    showCancelButton: true,
		    confirmButtonColor: "#DD6B55",
		    confirmButtonText: "Đồng ý",
		    cancelButtonText: "Không"
		}).then((result) => {
			if (result.isConfirmed) {
				$.ajax({
			        url: getContextPath() + '/inventory/goods-receipt/save',
			        type: 'POST',
					contentType: "application/json",
					data: JSON.stringify({
						inventoryId: $('#inventoryId').val(),
						parts: arr
					}),
					success: function(data) {
						console.log(data);
						if(data.status === 'success'){
							toastr.success(data.messages);
							$('#goods-receipt-form')[0].reset();
							
							window.setTimeout(function(){location.reload()},1000)
						}
						if(result.status === 'error'){
							toastr.error(data.messages);
						}
					}
		    	}); 
	    	}
		});
	}
});
