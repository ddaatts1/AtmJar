$(document).ready(function(){
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth() + 1; //January is 0!
	var yyyy = today.getFullYear();
	
	if (dd < 10) {
	   dd = '0' + dd;
	}
	
	if (mm < 10) {
	   mm = '0' + mm;
	} 
	    
	today = yyyy + '-' + mm + '-' + dd;
	document.getElementById("startTime").setAttribute("max", today);
	document.getElementById("endTime").setAttribute("max", today);
	
	function reverseDate(date){
		if (date == "" || date == null) {
			return null;
		}
		var dateSplit = date.split('/').reverse();
		return dateSplit[0] + '-' + dateSplit[1] + '-' + dateSplit[2];
		
	}
	
	$("#statistical-accessories").validate({
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
		},
		 errorElement: 'span',
	    errorPlacement: function (error, element) {
	      error.addClass('invalid-feedback');
	      element.closest('.form-group').append(error);
	    },
	    highlight: function (element, errorClass, validClass) {
	      $(element).addClass('is-invalid');
	    },
	    unhighlight: function (element, errorClass, validClass) {
	      $(element).removeClass('is-invalid');
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
	
	$(document).on('click', '#btn-export', function() {
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
				url: getContextPath() + "/export/statistical-accessory-excel",
				method: "POST",
				contentType: "application/json",
				xhrFields:{
	                responseType: 'blob'
	            },
				data: JSON.stringify({
					contractId: $('#contractId').val(),
					startTime: reverseDate($('#startTime').val()),
					endTime: reverseDate($('#endTime').val()),
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
				
					fileLink.download = 'Thongketheolinhkien_' + time;
					
					fileLink.click();
			        
			        //document.location = downloadUrl;
			        
			        toastr.success("Xuất báo cáo thành công!");
				}
			});
		  }
		})		
	}) 
})