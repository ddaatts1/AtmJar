$(document).ready(function(){
	$("#loading-statistical").hide();
	localStorage.clear();
	function loadFirstTime(id){
		$.ajax({
		url: getContextPath() + "/statistical/getContract",
		method: 'POST',
		contentType: "application/json",
        data: JSON.stringify({
			id: id
		}),
		success: function(result) {
			$('#startTime').val(result.startTime);
			$('#startTime').attr('min', result.startTime);
			$('#startTime').attr('max', result.endTime);
			
			$('#endTime').val(result.endTime);
			$('#endTime').attr('min', result.startTime);
			$('#endTime').attr('max', result.endTime);
			
			buttonOnClick();
		}
	});
	}
	
	loadFirstTime($("#contractId").val());
		
	function buttonOnClick(){
		$("#loading-statistical").show();
		localStorage.clear();
		$.ajax({
			url: getContextPath() + "/statistical",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				contractId: $('#contractId option:selected').val(),
				startTime: $('#startTime').val(),
				endTime: $('#endTime').val()
			}),
			success: function(result) {
				$("#loading-statistical").hide();
				
				$('#searchResult').empty();
				$('#btn-export').attr("disabled", false);
				if (result.length > 0) {
					$('#btn-export').removeAttr("hidden");
					var periodSize = result[0].periods.length;
					
					$('#searchResult').append('<div class="tbl_search_result">\n')
						.append('</div>');
					
					$('.tbl_search_result').append('<table class="table table-bordered table-hover">\n')
						.append('</table>');
						
					$('.table-hover').append('<tr class="title">\n')
						
					$('.title').append('<th class="text-center" rowspan="2">STT</th>\n')
						.append('<th class="text-center" rowspan="2">Serial number</th>\n')
						.append('<th class="text-center" rowspan="2">Địa chỉ</th>\n')
						.append('<th class="text-center" colspan="' + periodSize + '">Trạng thái bảo trì</th>\n')
						.append('<th class="text-center" rowspan="2">Service onsite</th>\n')
						.append('<th class="text-center" rowspan="2">Hd qua điện thoại</th>\n')
						.append('<th class="text-center" rowspan="2">Tổng số lần KPSC</th>\n')
						.append('</tr>\n');	
						
					$('.table-hover').append('<tr class="title" id="periods">\n')
						.append('</tr>\n')
						
						
					$.each(result[0].periods, function(index, item) {
						$('#periods').append('<th class="text-center">' + item.name + '</th>')
					})
					
					$.each(result, function(index, item) {
						$('.table-hover').append('<tr id="item-' + index + '">\n');
						$('#item-' + index).append('<td class="text-center">'+ (index + 1) +'</td>\n')
							.append('<td class="text-center"><a href="javascript:void(0);" class="btn-search-serial" data-id="'+ item.serialNumber +'">'+ item.serialNumber +'</a></td>\n')
							.append('<td>'+ item.address +'</td>\n');
						$.each(item.periods, function(inx, period) {
							if (period.maintenance) {
								$('#item-' + index).append('<td class="text-center maintained">Đã bảo trì</td>')
							}else {
								$('#item-' + index).append('<td></td>')
							}
						})
						$('#item-' + index).append('<td class="text-center">'+ (item.totalKpsc - item.remoteServices) +'</td>')
						$('#item-' + index).append('<td class="text-center">'+ item.remoteServices +'</td>')
						$('#item-' + index).append('<td class="text-center">'+ item.totalKpsc +'</td>')
						
					})
					
					localStorage.setItem("contractId", $('#contractId option:selected').val());
					localStorage.setItem("startTime", $('#startTime').val() ? $('#startTime').val() : '');
					localStorage.setItem("endTime", $('#endTime').val() ? $('#endTime').val() : '');
				}
			},
			error: function () {
	        $("#loading-statistical").hide();
	      }
		})
	}
	
	$('#btn-submit-form').on('click', function() {
		buttonOnClick();
	})
	
	$('#btn-export').on('click', function() {
		Swal.fire({
			title: "Bạn có chắc không?",
	        text: "Báo cáo sẽ được lưu về máy của bạn dưới dạng file excel!",
	        icon: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "Đồng ý",
	        cancelButtonText: "Không"
		}).then((data) => {
		  if (data.isConfirmed) {
			$.ajax({
				url: getContextPath() + "/export/cyclicalStatistics",
				method: "POST",
				contentType: "application/json",
				xhrFields:{
	                responseType: 'blob'
	            },
				data: JSON.stringify({
					contractId: localStorage.getItem("contractId") !== null ? localStorage.getItem("contractId") : "",
					startTime: localStorage.getItem("startTime") !== null ? localStorage.getItem("startTime") : "",
					endTime: localStorage.getItem("endTime") !== null ? localStorage.getItem("endTime") : ""
				}),
				success: function(result) {
					var blob = new Blob([result], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'});
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
				
					fileLink.download = 'Thongketheochuky_' + time;
					
					fileLink.click();
			        //document.location = downloadUrl;
			        
			        toastr.success("Xuất báo cáo thành công!");
				}
			});
		  }
		})		
	}) 
	
	$(document).on('click', '.btn-search-serial', function() {
		let serial = $(this).attr('data-id');
		let url = getContextPath() + "/search?serial=" + serial;
		window.open(url, "_blank");
	})
	
	$(document).on('change', "#contractId", function() {
		let id = $(this).val();
		loadFirstTime(id);
	});
});
