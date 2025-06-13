$(document).ready(function(){
	localStorage.clear();
	
	function formatDate(date) {
	    var d = new Date(date),
	        month = '' + (d.getMonth() + 1),
	        day = '' + d.getDate(),
	        year = d.getFullYear();
	
	    if (month.length < 2) month = '0' + month;
	    if (day.length < 2) day = '0' + day;
	
	    return [day, month, year].join('/');
	}
	
	var currentDate = new Date();
	var theFirst = new Date(currentDate.getFullYear(), 0, 1);
	var theLast = new Date(currentDate.getFullYear(), 11, 31);
	
	$('#startTime').val(formatDate(theFirst));
	$('#endTime').val(formatDate(theLast));
	
	$("#person-statistical").validate({
		onfocusout: false,
		onkeyup: false,
		onclick: false,
		rules: {
			"startTime": {
				formatTime: true,
			},
			"endTime": {
				formatTime: true,
				greaterThan: "#startTime"
			},
		}
	});
	
	$.validator.addMethod("formatTime", function (value, element) {
		    // put your own logic here, this is just a (crappy) example
		    return this.optional(element) || value.match(/^(?:(?:31(\/|-|\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\/|-|\.)(?:0?[13-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)?\d{2})$|^(?:29(\/|-|\.)0?2\3(?:(?:(?:1[6-9]|[2-9]\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\d|2[0-8])(\/|-|\.)(?:(?:0?[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)?\d{2})$/);
		}, "Hãy nhập đầy đủ ngày theo định dạng dd/mm/yyyy");
		
	jQuery.validator.addMethod("greaterThan", 
	function(value, element, params) {
		if(value != "" || $(params).val() != ""){
			var endDate = new Date(value.split("/").reverse().join("-"));
			var startDate = new Date($(params).val().split("/").reverse().join("-"));
			
			return endDate > startDate;
		}
	    return this.optional(element);
	},'Ngày kết thúc phải lớn hơn ngày bắt đầu');
	
	onButtonSubmit();
	
	function onButtonSubmit(){
		localStorage.clear();
		$.ajax({
			url: getContextPath() + "/statistical-amount-per-person",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				startTime: reverseDate($('#startTime').val()),
				endTime: reverseDate($('#endTime').val())
			}),
			success: function(result) {
				$('#searchResult').empty();
				$('#btn-export').attr("disabled", false);
				if (result.length > 0) {
					$('#btn-export').removeAttr("hidden");
				}
				$('#searchResult').append('<div class="tbl_search_result"></div>');
				
				$('.tbl_search_result').append('<table class="table table-bordered table-hover" id="tbl-data"></table>')
				
				$('#tbl-data').append('<tr class="title"></tr>')
				
				$('.title').append('<th style="text-align: center;">STT</th>\n')
					.append('<th style="text-align: center;">Cán bộ</th>\n')
					.append('<th style="text-align: center;">Tổng số service</th>\n')
					.append('<th style="text-align: center;">Số service onsite</th>\n')
					.append('<th style="text-align: center;">Hướng dẫn qua điện thoại</th>\n')
					.append('<th style="text-align: center;">Số linh kiện thay thế</th>\n')
					.append('<th style="text-align: center;">Chi tiết</th>\n')
				
				$.each(result, function(index, item) {
					let onsiteService = item.totalServices - item.totalMobile;
					
					$('#tbl-data').append('<tr id="item-'+ index +'"></tr>')
					
					$('#item-' + index).append('<td style="text-align: center;">'+ (index + 1) +'</td>\n')
						.append('<td style="text-align: center;">'+ item.username +'</td>\n')
						.append('<td style="text-align: center; font-weight: bold;">'+ (item.totalServices == null ? '0' : item.totalServices)+'</td>\n')
						.append('<td style="text-align: center; font-weight: bold;">'+ onsiteService +'</td>\n')
						.append('<td style="text-align: center; font-weight: bold;">'+ (item.totalMobile == null ? '0' : item.totalMobile) +'</td>\n')
						.append('<td style="text-align: center; font-weight: bold;">'+ (item.totalAccessories == null ? '0' : item.totalAccessories) +'</td>\n')
						.append('<td style="text-align: center;"><a class="btn-view" data-id="'+item.username+'">Xem chi tiết</a></td>\n')
				})
								
				localStorage.setItem("startTime", $('#startTime').val() ? $('#startTime').val() : '');
				localStorage.setItem("endTime", $('#endTime').val() ? $('#endTime').val() : '');
			}
		})
	}
	
	$('#btn-submit-form').on('click', function() {
		onButtonSubmit();
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
				url: getContextPath() + "/export/person-amout-services",
				method: "POST",
				contentType: "application/json",
				xhrFields:{
	                responseType: 'blob'
	            },
				data: JSON.stringify({
					startTime: localStorage.getItem("startTime") !== null ? reverseDate(localStorage.getItem("startTime")) : "",
					endTime: localStorage.getItem("endTime") !== null ? reverseDate(localStorage.getItem("endTime")) : ""
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
				
					fileLink.download = 'Thongketheocanbo_' + time;
					
					fileLink.click();
			        
			        toastr.success("Xuất báo cáo thành công!");
				}
			});
		  }
		})		
	})
	
	function reverseDate(date){
		if (date == "" || date == null) {
			return null;
		}
		var dateSplit = date.split('/').reverse();
		return dateSplit[0] + '-' + dateSplit[1] + '-' + dateSplit[2];
		
	}
	
	function getServiceDetails(username){
		$.ajax({
			type: "GET",
			contentType: "text/plain",
			url: getContextPath() + "/statistical/services",
            data: {
				username: username,
				startTime: reverseDate(localStorage.getItem("startTime")),
				endTime: reverseDate(localStorage.getItem("endTime"))
			},
			success: function(result){
				console.log(result)
				let stt = 0;
				$('#tblATM').dataTable({
					"lengthChange": true,
					"searching": true,
					"destroy": true,
					"data": result,
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
								return data.time;
							}
						},
						{
							data: null,
							render: function(data){
//								return data.serialNumber;
								let txt = '';
								txt += '<a href="javascript:void(0)" title="Xem chi tiết" class="btn_show_detail" data-serial-number="'+ data.serialNumber +'">'+ data.serialNumber +'</a>';
								return txt;
							}
						},
						{
							data: null,
							render: function(data){
								return data.address;
							}
						},
						{
							data: null,
							render: function(data){
								return data.isMaintenance;
							}
						},
						{
							data: null,
							render: function(data){
								return data.status;
							}
						},
					]
				})
			},
			error: function(err, result) {
				toastr.error(result);
			}
		});
	}
	
	$(document).on('click', '.btn-view', function(){
		let username = $(this).attr('data-id');
		getServiceDetails(username);
		$('#modal_services').modal('show');
	})
	
	$(document).on('click', '.btn_show_detail', function(){
		let serial = $(this).attr('data-serial-number');
		let url = getContextPath() + "/search?serial=" + serial;
		
		window.open(url, "_blank");
	})
	
});
