$(document).ready(function(){
	$('#accessory').select2();
})

function showModal(jobId) {

	function formatDate(oldDateStr) {
		if (!oldDateStr) return ''; // Handle null or empty input

		// Split old date string into time and date parts
		var [time, date] = oldDateStr.split(' - ');
		var [day, month, year] = date.split('-');

		// Create a new Date object using parsed values
		var formattedDate = new Date(`${year}-${month}-${day}T${time}:00`);

		// Format the new date
		var dayFormatted = ("0" + formattedDate.getDate()).slice(-2);
		var monthFormatted = ("0" + (formattedDate.getMonth() + 1)).slice(-2);
		var yearFormatted = formattedDate.getFullYear();
		var hoursFormatted = ("0" + formattedDate.getHours()).slice(-2);
		var minutesFormatted = ("0" + formattedDate.getMinutes()).slice(-2);

		return dayFormatted + '/' + monthFormatted + '/' + yearFormatted + ' ' + hoursFormatted + ':' + minutesFormatted;
	}
	//Get data
	$.ajax({
		url: getContextPath() + "/getJob",
		method: 'GET',
		data: {
			jobId: jobId
		},
		success: function(result) {
			// set thông tin services
			var modal_user_val = '';
			if (result.username !== null) {
				modal_user_val = modal_user_val.concat(result.username);
			}
			if (result.phoneNumber !== null) {
				modal_user_val = modal_user_val.concat(' / Sđt: ').concat(result.phoneNumber);
			}
			$('#modal-user').text(modal_user_val);
			$('#modal-serial').text(result.serialNumber);
			$('#modal-address').text(result.address);
			// $('#modal-startTime').text(result.checkInTime);
			// $('#modal-endTime').text(result.checkOutTime);
			$('#modal-startTime').text(formatDate(result.checkInTime));
			$('#modal-endTime').text(formatDate(result.completeTime));
			
			if (result.note !== null) {
				document.getElementById("td-note").style.backgroundColor = "lightsalmon";
				$('#modal-note').text(result.note);
			}else {
				document.getElementById("td-note").style.backgroundColor = "none";
			}
			
			
			$('#modal-workDetail').text(result.workDetail);
			
			$.each(result.kpscs, function(index, item) {
				$('#tr-kpsc').after('<tr class="kpsc-item"><td>'+ (result.kpscs.length - index) +'</td><td colspan="1">'+ item.device +'</td><td colspan="4">'+ item.errorDesc +'</td><td colspan="4">' + item.jobPerform + '</td></tr>');
			});
			
			$.each(result.accessories, function(index, item) {
				$('#tr-accessories').after('<tr class="accessory-item"><td>'+ (result.accessories.length - index) +'</td><td colspan="1">'+ item.device +'</td><td colspan="4">'+ item.desc +'</td><td colspan="4"></td></tr>');
			});
			
			$('#modal-lg').modal({
			    backdrop: 'static',
			    keyboard: false
			})
			$('#modal-lg').modal('show');
		}
	});
	
}

	function reverseDate(date){
		var dateSplit = date.split('/').reverse();
		return dateSplit[0] + '-' + dateSplit[1] + '-' + dateSplit[2];
	}

function closeModal() {
	$('#modal-user').text('');
	$('#modal-serial').text('');
	$('#modal-address').text('');
	$('#modal-startTime').text('');
	$('#modal-endTime').text('');
	$('#modal-note').text('');
	
	$('.kpsc-item').remove();
	$('.accessory-item').remove();
	
	$('#modal-lg').modal('hide');
}

function deleteJob(id) {
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
		location.href = getContextPath() + '/job/delete?id=' + id;
	  }
	})
}

function deleteAllJob() {
	Swal.fire({
		title: "Bạn có chắc không?",
        text: "Tất cả bản ghi sẽ được xóa khỏi kho dữ liệu!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Không"
	}).then((data) => {
	  if (data.isConfirmed) {
		var path = getContextPath() + '/job/deleteAll?';
		if ($('#username').val() !== '') {
			path = path.concat('username=').concat($('#username').val()).concat('&');
		}
		if ($('#startTime').val() !== '') {
			path = path.concat('startTime=').concat($('#startTime').val()).concat('&');
		}
		if ($('#endTime').val() !== '') {
			path = path.concat('endTime=').concat($('#endTime').val()).concat('&');
		}
		if ($('#status').val() !== '') {
			path = path.concat('status=').concat($('#status').val());
		}
	 	location.href = path;
	  }
	})
}

function deleteJobOnSearchView(id) {
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
		location.href = getContextPath() + '/search/delete?id=' + id;
	  }
	})
}

function deleteAllJobOnSearchView() {
	Swal.fire({
		title: "Bạn có chắc không?",
        text: "Tất cả bản ghi sẽ được xóa khỏi kho dữ liệu!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Không"
	}).then((data) => {
	  if (data.isConfirmed) {
		var path = getContextPath() + '/search/deleteAll?';
		if ($('#serial').val() !== '' && $('#serial').val() !== undefined) {
			path = path.concat('serial=').concat($('#serial').val()).concat('&');
		}
		if ($('#address').val() !== '' && $('#address').val() !== undefined) {
			path = path.concat('address=').concat($('#address').val()).concat('&');
		}
		if ($('#startTime').val() !== '' && $('#startTime').val() !== undefined) {
			path = path.concat('startTime=').concat($('#startTime').val()).concat('&');
		}
		if ($('#endTime').val() !== '' && $('#endTime').val() !== undefined) {
			path = path.concat('endTime=').concat($('#endTime').val().concat('&'));
		}
		if ($('#status').val() !== '') {
			path = path.concat('status=').concat($('#status').val());
		}
	 	location.href = path;
	  }
	})
}

$(document).on('click', '.btn-search-serial', function() {
	var serial = $(this).attr('data-id');
	let url = getContextPath() + "/search?serial=" + serial;
		
	window.open(url, "_blank");
})

$(document).on('click', '.btn-take-report', function() {
	var id = $(this).attr('data-id');
	Swal.fire({
		title: "Bạn có chắc không?",
		text: 'Đã nhận được báo cáo bản cứng từ cán bộ!',
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#DD6B55",
		confirmButtonText: "Đồng ý",
		cancelButtonText: "Không"
	}).then((data) => {
		if (data.isConfirmed) {
			$.ajax({
				url: getContextPath() + "/jobs/take-report",
				method: "POST",
				contentType: "application/json",
				data: JSON.stringify({
					id: id
				}),
				success: function(result) {
					result = jQuery.parseJSON(result);
					if (result.status === 'success'){
						toastr.success(result.messages);
						window.setTimeout(function(){location.reload()}, 1000);
					}
					if(result.status === 'error') {
						toastr.error(result.messages);
						window.setTimeout(function(){location.reload()}, 1000);
					}
				}
			});
		}
	});
})

$(document).on('click', '#btn-export', function() {
	var serial = $('#serial').val();
	var address = $('#address').val();
	var status = $('#status option:selected').val();
	var startTime = $('#startTime').val();
	console.log("startTime " + startTime);
	var endTime = $('#endTime').val();
	var accessory = $('#accessory option:selected').val();
	let serviceType = $('#serviceType option:selected').val();
	Swal.fire({
		title: "Bạn có chắc không?",
		text: 'Các bản ghi sẽ được xuất ra file excel!',
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#DD6B55",
		confirmButtonText: "Đồng ý",
		cancelButtonText: "Không"
	}).then((data) => {
		if (data.isConfirmed) {
			$.ajax({
				url: getContextPath() + "/search/export-excel",
				method: "POST",
				contentType: "application/json",
				xhrFields: {
					responseType: 'blob'
				},
				data: JSON.stringify({
					serial: serial,
					address: address,
					status: status,
					startTime: startTime,
					endTime: endTime,
					accessory: accessory,
					serviceType: serviceType
				}),
				success: function(result) {
				if(result.size>3800){
					
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
				
					fileLink.download = 'TraCuuService_' + time;
					
					fileLink.click();
					toastr.success("Xuất báo cáo thành công!");
				}
				else{
					toastr.error("Lỗi");
				}
					
				}
			});
		}
	});
})

const notifi = Swal.mixin({
	toast: true,
	position: 'top-end',
	showConfirmButton: false,
	timer: 2000,
	timerProgressBar: true,
	didOpen: (toast) => {
		toast.addEventListener('mouseenter', Swal.stopTimer)
		toast.addEventListener('mouseleave', Swal.resumeTimer)
	}
});

// Open modal edit checkin, checkout 
var openModalJobTime = function (job) {
	$('#modalTimeUser').text(job.user);
	$('#modalTimeSerial').text(job.serialNumber);
	$('#modalTimeAddress').text(job.address);
	$('#modalTimeWorkDetail').text(job.workDetail);
	$('#modalTimeCheckIn').val(job.checkInStr);
	$('#modalTimeCheckOut').val(job.checkOutStr);
	
	$('#modalJobTime').modal({
		backdrop: 'static',
		keyboard: false,
		show: true
	});
}

// Admin edit checkin, checkout
$(document).on('click', '#btnModalJobTime', function () {
	let jobId = $(this).attr('data-id');
	$('#jobId').val(jobId);
	$.ajax({
		url: getContextPath() + "/jobs/get-time",
		method: "GET",
		data: {
			jobId: jobId
		},
		success: function (result) {
			console.log(result);
			openModalJobTime(result);
		},
		error: function (err) {
			console.log(err)
		}
	});
})

// Submit new CheckIn and Checkout 
$(document).on('click', '#btnEditJobTime', function () {
	let jobId = $('#jobId').val();
	let checkIn = $('#modalTimeCheckIn').val();
	let checkOut = $('#modalTimeCheckOut').val();
	
	if (!Date.parse(checkIn) || !Date.parse(checkOut)) {
		notifi.fire({
			icon: 'error',
			title: 'Thời gian bắt đầu, thời gian kết thúc không được bỏ trống!'
		});
	}else {
		Swal.fire({
			title: "Confirm?",
			icon: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "Yes",
			cancelButtonText: "No"
		}).then((confirm) => {
			if (confirm.isConfirmed) {
				$.ajax({
					url: getContextPath() + "/jobs/edit-time",
					method: "POST",
					data: {
						jobId: jobId,
						checkIn: checkIn,
						checkOut: checkOut,
					},
					success: function (result) {
						console.log(result);
						if (result !== undefined && result != null) {
							$('#modalJobTime').modal('hide');
							notifi.fire({
								icon: 'success',
								title: 'Success!'
							});
						}else {
							notifi.fire({
								icon: 'error',
								title: 'Failed!'
							});
						}
					},
					error: function (err) {
						console.log(err);
						notifi.fire({
							icon: 'error',
							title: 'Failed!'
						});
					}
				})
			}
		});
	}
})