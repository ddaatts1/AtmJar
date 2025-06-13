$(document).ready(function() {
	
});

$(document).on('click', '.btn-view-issue', function() {
	var id = $(this).attr('data-id');
	console.log("id " + id);
	
	$.ajax({
		url: getContextPath() + "/inventory/history/view",
		method: 'GET',
		contentType: "application/json",
		data: {
			id: id
		},
		success: function(result) {
			if (result != null) {
				let inventoryId = $('#inventoryId').val();
				$('#issue-trackingId').val(result.id);
				$('#issue-sender').text(result.sender);
				$('#issue-status').text(result.statusStr);
				$('#issue-receiver').text(result.receiver);
				$('#issue-send-date').text(result.sendDateStr);
				$('#issue-received-date').text(result.receivedDateStr);
				$('#issue-phone-number').text(result.receivedPhone);
				$('#issue-department').text(result.receivedDepartment);
				$('#issue-address').text(result.receivedAddress);
				$('#issue-content').text(result.note);
				
				$.each(result.trackingDetails, function(index, item) {
					$('#tr-issue-detail').after('<tr class="issue-item"><td colspan="4" class="text-center">'+ item.typeStr +'</td><td colspan="4" class="text-center">'+ item.partName +'</td><td colspan="4" class="text-center">' + item.quantity + '</td></tr>');
				});
				
				if (inventoryId == result.receivedInventory && result.status == 0) {
					$('#btn-confirm').removeAttr('hidden');
				}else {
					$('#btn-confirm').attr('hidden', true);
				}
				$('#modal-view-issue').modal({
					show: true
				})
			}
		}
	})
})

$(document).on('click', '.btn-view-receipt', function() {
	var id = $(this).attr('data-id');
	
	$.ajax({
		url: getContextPath() + "/inventory/history/view",
		method: 'GET',
		contentType: "application/json",
		data: {
			id: id
		},
		success: function(result) {
			if (result != null) {
				$('#receipt-trackingId').val(result.id);
				$('#receipt-receiver').text(result.receiver);
				$('#receipt-received-date').text(result.receivedDateStr);
				
				$.each(result.trackingDetails, function(index, item) {
					$('#tr-receipt-detail').after('<tr class="receipt-item"><td colspan="4" class="text-center">'+ item.typeStr +'</td><td colspan="4" class="text-center">'+ item.partName +'</td><td colspan="4" class="text-center">' + item.quantity + '</td></tr>');
				});
				
				$('#modal-view-receipt').modal({
					show: true
				})
			}
		}
	})
})

function closeModal() {
	$('#issue-trackingId').val('');
	$('#issue-sender').text('');
	$('#issue-status').text('');
	$('#issue-receiver').text('');
	$('#issue-send-date').text('');
	$('#issue-received-date').text('');
	$('#issue-phone-number').text('');
	$('#issue-department').text('');
	$('#issue-address').text('');
	$('#issue-content').text('');
	
	$('#receipt-trackingId').val('');
	$('#receipt-receiver').text('');
	$('#receipt-received-date').text('');

	$('.issue-item').remove();
	$('.receipt-item').remove();
	
	$('#modal-view-issue').modal('hide');
	$('#modal-view-receipt').modal('hide');
}

$(document).on('click', '#btn-confirm', function() {
	let trackingId = $('#issue-trackingId').val();
	console.log("Tracking id === " + trackingId);
	Swal.fire({
		title: "Bạn có chắc không?",
//	    text: "Bản ghi sẽ được xóa khỏi kho dữ liệu!",
	    icon: "warning",
	    showCancelButton: true,
	    confirmButtonColor: "#DD6B55",
	    confirmButtonText: "Đồng ý",
	    cancelButtonText: "Không"
	}).then((data) => {
		if (data.isConfirmed) {
			$.ajax({
		        url: getContextPath() + '/inventory/goods-issue/update',
		        type: 'POST',
				contentType: "application/json",
				data: JSON.stringify({
					trackingId: trackingId,
				}),
				success: function(result) {
					if(result.status === 'success'){
						toastr.success(result.messages);
						window.setTimeout(function(){location.reload()},1000)
					}
					if(result.status === 'error'){
						toastr.error(result.messages);
					}
				}
			});	
		}
	})
});