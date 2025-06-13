$(document).ready(function(){
	$("#form_Search").validate({
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
		
	function loadDataInregionservice() {
		$.ajax({
			url: getContextPath() + "/statistical-region/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				regionId: $('#regionId').children("option:selected").val(),
				departmentId: $('#departmentId').children("option:selected").val(),

			}),
			success: function(result) {
				$('#departmentId').empty();
				let html = '';
				html += `
				
				<option value="">------ Tất cả đơn vị ------</option>
				
				`;
				for (let item of result) {
					let selected = '';
					if ((item['id'] == $('#departmentParam').val())) {
						selected = 'selected';
					}
					html += `
					<option value="`+ item['id'] + `"`+ selected +`>
					`+ item['name'] + `
					</option>
					`;
				}

				$('#departmentId').append(html);
			}
		})
	}
	
	loadDataInregionservice()
	$('#regionId').on('change', function() {
		loadDataInregionservice()
	})
	
	function loadDataIndepartmenservice() {
		$.ajax({
			url: getContextPath() + "/statistical-departmen/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				regionId: $('#regionId').children("option:selected").val(),
				departmentId: $('#departmentId').children("option:selected").val()==null?'':$('#departmentId').children("option:selected").val(),
			}),
			
			success: function(result) {
				$('#regionId').empty();
				let html = '';
				let departmentIditem= $('#departmentId').children("option:selected").val();
				if(departmentIditem=='') {
					html += `
					<option value=""  selected = 'selected' >------ Tất cả vùng miền ------</option>`;
				}
				for (let item of result) {
					let selected = '';
					if ((item['id'] == $('#regionParam').val())) {
						selected = 'selected';
					}
					html += `<option value="`+ item['id'] + `"`+ selected +`>
					`+ item['name'] + `</option>`;
				}
				$('#regionId').append(html);
			}
		})
	}
	
	loadDataIndepartmenservice()
	
	$('#departmentId').on('change', function() {
		loadDataIndepartmenservice();
	
	})
	
	function getData(){
		$.ajax({
            url: getContextPath() + '/statistical-services/get-data-chart',
            type: 'POST',
			contentType: "application/json",
            data: JSON.stringify({
				contractId: $('#contractId').children("option:selected").val(),
				regionId: $('#regionParam').val() == '' ? null : $('#regionParam').val(),
				departmentId: $('#departmentParam').val(),
				startTime: $('#startTime').val(),
				endTime: $('#endTime').val(),
			}),
            success: function(result){
				let category = result.months;
				let series = result.series;
				loadChart(category, series);
            }
        });
	}
	getData();
	
	function loadChart(category, series){
		Highcharts.chart('chart_content', {
		  chart: {
		    type: 'line'
		  },
		  title: {
		    text: ''
		  },
		  subtitle: {
		    text: ''
		  },
		  xAxis: {
		    categories: category
		  },
		  yAxis: {
		    title: {
		      text: ''
		    }
		  },
		  plotOptions: {
		    line: {
		      dataLabels: {
		        enabled: true
		      },
		      enableMouseTracking: false
		    }
		  },
		  series: series
		});
	}
	
	$('#btn-show-chart').on('click', function() {
		$('.chart_div').removeAttr("hidden");
		$('#btn-hide-chart').removeAttr("hidden");
		$('#btn-show-chart').attr("hidden",true);
	})
	
	$('#btn-hide-chart').on('click', function() {
		$('#btn-show-chart').removeAttr("hidden");
		$('.chart_div').attr("hidden", true);
		$('#btn-hide-chart').attr("hidden", true);
	})
})

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
        url: getContextPath() + '/export/statisticalByMonth',
        method: "POST",
		contentType: "application/json",
		xhrFields:{
            responseType: 'blob'
        },
        data: JSON.stringify({
			contractId: $('#contractId').children("option:selected").val(),
			regionId: $('#regionId').children("option:selected").val(),
			departmentId: $('#departmentId').children("option:selected").val(),
			startTime: $('#startTime').val(),
			endTime: $('#endTime').val(),
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
			
				fileLink.download = 'Thong-ke-services-' + time;
				fileLink.click();
		        toastr.success("Xuất báo cáo thành công!");
			}
    	});
	  }
	})		
}) 