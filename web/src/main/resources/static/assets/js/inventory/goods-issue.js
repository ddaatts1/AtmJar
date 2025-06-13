$(document).ready(function(){
	localStorage.clear();
	genDataTable([]);
	getPartByType();
	
	$('form input').keydown(function (e) {
	    if (e.keyCode == 13) {
	        e.preventDefault();
	        return false;
	    }
	});
	
	$.validator.addMethod('customphone', function (value, element) {
	    return this.optional(element) || /((^(\+84|84|0|0084){1})(3|5|7|8|9))+([0-9]{8})$/.test(value);
	}, "Số điện thoại không đúng định dạng.");
	
	$("#goods-issue-form").validate({
		onfocusout: false,
      	onkeyup: false,
      	onclick: false,
      	errorElement: 'span',
      	rules: {
			"receiver": {
          		required: true,
        	},
        	"phone_number": 'customphone',
        	"received_address": {
          		required: true,
        	},
    	},
    	messages: {
			"receiver": {
          		required: "Vui lòng nhập tên người nhận.",
        	},
			"phone_number": {
          		required: "Vui lòng nhập số điện thoại người nhận.",
        	},
			"received_address": {
          		required: "Vui lòng nhập địa chỉ người nhận.",
        	},
    	}
	});
	
	// Gợi ý người nhận
	$( "#receiver" ).autocomplete({
      source: function( request, response ) {
        $.ajax({
          	url: getContextPath() + "/user-suggestion",
			method: "GET",
			contentType: "application/json",
	        success: function( data ) {
	        	response( data );
	        }
        });
      },
      minLength: 0,
      select: function( event, ui ) {
      	$.ajax({
			url: getContextPath() + "/selected-suggestion",
			method: "GET",
			data: { key: ui.item.label },
			success: function(result) {
				if (result != null) {
					console.log(result);
					$('#receiver').val(result.name);
					$('#phone_number').val(result.phone);
					$('#received_department').val(result.department);
					$('#received_address').val(result.address);
				}else {
					console.log("failed");
				}
			}
		})
      },
      open: function() {
        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
      },
      close: function() {
        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
      }
    });
});

// Filter part
$(document).on('change', '#part_type', function() {
	getPartByType();
});

function getPartByType() {
	$.ajax({
		url: getContextPath() + '/inventories/get-by-type-and-quantity-gt-zero',
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

$(document).on('change', '#parts', function() {
	var partVal = $('#parts').val();
	if (partVal !== '') {
		$.ajax({
			url: getContextPath() + '/inventories/getQuantity',
			type: 'GET',
			contentType: "application/json",
			data: {
				partId: partVal
			},
			success: function(result) {
				$('#quantity').removeAttr('disabled');
				$('#quantity').attr('max', result);
			}
		});
	}
});

// Add part modal nhập xuất
$(document).on('click', '#btn-add-part', function() {
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
		var checkMax = false;
		// Max quantity 
		var maxVal = $('#quantity').attr('max');
			
		if (arr.length > 0) {
			for (let i = 0; i < arr.length; i++) {
				if (part.partId === arr[i].partId) {
					let newQuantity = Number(part.quantity) + Number(arr[i].quantity);
					if(Number(newQuantity) <= Number(maxVal)) {
						part.quantity = newQuantity;
						index = i;
					}else {
						checkMax = true;
					}
				}
			}
		}else {
			if (Number(part.quantity) > Number(maxVal))
				checkMax = true;
		}
		
		if (!checkMax) {
			if (index >= 0) {
				arr.splice(index, 1, part);
			}else {
				arr.push(part);
			}
			localStorage.setItem('parts', JSON.stringify(arr));
			genDataTable(arr);
			$('#quantity').val('');
		}else {
			toastr.error('Trong kho chỉ có ' + maxVal + ' part');
		}
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
//					let txt = '<input type="text" class="quantity" data-index="'+ stt +'" data-id="'+ data.quantity +'" value="'+ data.quantity +'"></td>';
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

$(document).on('submit', '#goods-issue-form', function(e) {
	e.preventDefault();
	var form = $(this);
  	form.valid();
  	let check = true;
	var arr = [];
	if (localStorage.getItem('parts') === null || JSON.parse(localStorage.getItem('parts')).length <= 0) {
		check = false;
		toastr.error("Chưa có thông tin part xuất khỏi kho.");
	}else {
		arr = JSON.parse(localStorage.getItem('parts'));
	}
	
	if(check){
		Swal.fire({
			title: "Bạn có chắc không?",
//		    text: "Bản ghi sẽ được xóa khỏi kho dữ liệu!",
		    icon: "warning",
		    showCancelButton: true,
		    confirmButtonColor: "#DD6B55",
		    confirmButtonText: "Đồng ý",
		    cancelButtonText: "Không"
		}).then((data) => {
			if (data.isConfirmed) {
				$.ajax({
			        url: getContextPath() + '/inventory/goods-issue/save',
			        type: 'POST',
					contentType: "application/json",
					data: JSON.stringify({
						inventoryId: $('#inventoryId').val(),
						receivedInventory: $('#received_inventory option:selected').val(),
						receiver: $('#receiver').val(),
						phoneNumber: $('#phone_number').val(),
						receivedDepartment: $('#received_department').val(),
						receivedAddress: $('#received_address').val(),
						note: $('#note').val(),
						parts: arr
					}),
					success: function(result) {
						console.log(result);
						if(result.status === 'success'){
							toastr.success(result.messages);
							$('#goods-issue-form')[0].reset();
							
							if (result.isOpenNewTab) {
								let url = getContextPath() + "/inventories/thong-tin-nguoi-nhan?id=" + result.trackingId;
								
							    window.open(url);
							}
							window.setTimeout(function(){location.reload()},1000)
						}
						if(result.status === 'error'){
							toastr.error(result.messages);
						}
					}
				});
			}
    	}); 
	}
});