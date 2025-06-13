$(document).ready(function() {
	localStorage.clear();
	
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
					url: getContextPath() + "/export/amout-services",
					method: "POST",
					contentType: "application/json",
					xhrFields: {
						responseType: 'blob'
					},
					data: JSON.stringify({
						contractId: $('#contractId').val(),
						regionId: $('#regionId option:selected').val(),
						departmentId: $('#departmentId option:selected').val(),
						startTime: $('#startTime').val(),
						endTime: $('#endTime').val(),
						status: $('#btn-submit-forms').val()
					}),
					success: function(result) {
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
					
						fileLink.download = 'Thongkesoluongmay_' + time;
						
						fileLink.click();

						toastr.success("Xuất báo cáo thành công!");
					}
				});
			}
		})
	})
	
	function loadDataInTable() {
		localStorage.clear();
		// Load data lúc mới vào
		$.ajax({
			url: getContextPath() + "/statistical-amountRegion/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				contractId: $('#contractId').val(),
				regionId: $('#regionId option:selected').val(),
				departmentId: $('#departmentId option:selected').val(),
			}),
			success: function(result) {
				$('#searchResult').empty();
				$('#btn-export').attr("hidden", true);
				if (result.length > 0) {
					$('#btn-export').removeAttr("hidden");
					$('#tota').show();
				}
				if (result.length > 0) {
					$('#searchResult').append('<div class="tbl_search_result"></div>');
					$('.tbl_search_result').append('<table class="table table-bordered table-hover" id="tbl-data"></table>')
					$('#tbl-data').append('<tr class="title"></tr>')
					$('.title').append('<th style="text-align: center;">STT</th>\n')

						.append('<th style="text-align: center;">Vùng Miền</th>\n')
						.append('<th style="text-align: center;">Số lượng máy</th>\n')
						.append('<th style="text-align: center;">Hợp đồng</th>\n')
						.append('<th style="text-align: center;">Số lượng máy</th>\n')

				}else {
					$('#searchResult').append('<div class="tbl_search_result"><center><i>không tìm thấy báo cáo số lượng máy</i></center></div>');
				}
				let html = '';
				let totalAtm = 0;
				for (let i = 0; i < result.length; i++) {

					let result_child = result[i]["statisticalContract"];
					
					let expanded = '';
					
					if (result_child.length > 4) {
						expanded = `
							<tr id="expanded-`+ i +`">
								<td style="text-align: center;" colspan=2>
									<a class="btn-expanded" href="javascript:void(0)" data-id="`+ i +`" data-length="`+ (result_child.length + 1) +`">Xem thêm &nbsp<em class="fas fa-chevron-down"></em></a>
								</td>
							</tr>
							<tr id="collapsed-`+ i +`" hidden>
								<td style="text-align: center;" colspan=2>
									<a class="btn-collapsed" href="javascript:void(0)" data-id="`+ i +`" data-length="`+ (result_child.length + 1) +`">Thu gọn &nbsp<em class="fas fa-chevron-up"></em></a>
								</td>
							</tr>
						`;
						
						for (let j = 0; j < result_child.length; j++) {
							let col1 = '';
							let col2 = '';
							let col3 = '';
							if (j == 0) {
								col1 = `<td style="text-align: center;" class="firstRow-`+ i +`" rowspan="5">` + parseFloat(i + 1) + `</td>`;
								col2 = `<td style="text-align: center;" class="firstRow-`+ i +`" rowspan="5">` + result[i]['regionName'] + `</td>`;
								col3 = `<td style="text-align: center;" class="firstRow-`+ i +`" rowspan="5">` + result[i]['countAtm'] + `</td>`;
								
								totalAtm += parseFloat(result[i]['countAtm']);
							}
							
							if (j <= 3) {
								html += `
									<tr class="tr-`+ i +`" data-index="`+ j +`">
						       		     	`+ col1 + `
							            	`+ col2 + `
							             `+ col3 + `
										<td style="text-align: left;">`+ result_child[j]['contractName'] + `</td>
										<td style="text-align: center;">`+ result_child[j]['countAtm'] + `</td>
									</tr>
								`;
							}else {
								html += `
									<tr class="tr-`+ i +`" data-index="`+ j +`" hidden>
						       		     	`+ col1 + `
							            	`+ col2 + `
							             `+ col3 + `
										<td style="text-align: left;">`+ result_child[j]['contractName'] + `</td>
										<td style="text-align: center;">`+ result_child[j]['countAtm'] + `</td>
									</tr>
								`;
							}
						}
					}else {
						for (let j = 0; j < result_child.length; j++) {
							let col1 = '';
							let col2 = '';
							let col3 = '';
							if (j == 0) {
								col1 = `<td style="text-align: center;" rowspan="` + result_child.length + `">` + parseFloat(i + 1) + `</td>`;
								col2 = `<td style="text-align: center;" rowspan="` + result_child.length + `">` + result[i]['regionName'] + `</td>`;
								col3 = `<td style="text-align: center;" rowspan="` + result_child.length + `">` + result[i]['countAtm'] + `</td>`;
							}
							html += `
								<tr class="tr-`+ i +`" data-index="`+ j +`">
					       		     	`+ col1 + `
						            	`+ col2 + `
						             `+ col3 + `
									<td style="text-align: left;">`+ result_child[j]['contractName'] + `</td>
									<td style="text-align: center;">`+ result_child[j]['countAtm'] + `</td>
								</tr>
							`;
						}
					}
					
					html += expanded;
				}
				html += `<tr class="tr-total">
					<td class="text-center" style="font-weight: bold;" colspan="2">Tổng: </td>
					<td class="text-center" style="font-weight: bold;">`+ totalAtm +`</td>
				</tr>`; 

				$('#tbl-data').append(html);
				localStorage.setItem("contractId", $('#contractId').val());
				localStorage.setItem("regionId", $('#regionId option:selected').val());
				localStorage.setItem("departmentId", $('#departmentId option:selected').val());
				localStorage.setItem("startTime", $('#startTime').val());
				localStorage.setItem("endTime", $('#endTime').val());
			}
		})
	}
	loadDataInTable()
	$('#btn-submit-form').on('click', function() {
		$("#form-search").validate({
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
		    return new Date(value) > new Date($(params).val());
		},'Ngày kết thúc phải lớn hơn ngày bắt đầu');
	
		loadDataInTable()
	});

	$('#btn-submit-forms').on('click', function() {
		localStorage.clear();
		$.ajax({
			url: getContextPath() + "/statistical-amountRegion/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				contractId: $('#contractId').val(),
				regionId: $('#regionId option:selected').val(),
				departmentId: $('#departmentId option:selected').val(),
				startTime: $('#startTime').val(),
				endTime: $('#endTime').val(),
				status: $('#btn-submit-forms').val()
			}),
			success: function(result) {
				$('#searchResult').empty();
				$('#btn-export').attr("hidden", true);

				if (result.length > 0) {
					$('#btn-export').removeAttr("hidden");
					$('#tota').show();
					
					$('#searchResult').append('<div class="tbl_search_result"></div>');

					$('.tbl_search_result').append('<table class="table table-bordered table-hover" id="tbl-data"></table>')

					$('#tbl-data').append('<tr class="title"></tr>')
				}
				else {
					$('#searchResult').append('<div class="tbl_search_result"><center><i>không tìm thấy báo cáo thống kê số lượng máy</i></center></div>');
				}

				let html = '';
				let totalAtm = 0;
				let totalService = 0;
				let totalAccessory = 0;

				for (let item of result) {
					totalAtm += parseFloat(item['countAtm']);
					totalService += parseFloat(item['countService']);
					totalAccessory += parseFloat(item['sumQuantity']);
				}
				html += `
				<tr>
					<th>Tổng số lượng máy</th>
					<td class="text-center" id="totalAtm">`+ totalAtm + `</td>
				</tr>
				<tr>
					<th>Tổng số lần services</th>
					<td class="text-center" id="totalService">`+ totalService + `</td>
				</tr>
				<tr>
					<th>Tổng số linh kiện thay thế</th>
					<td class="text-center" id="totalAccessory">`+ totalAccessory + `</td>
				</tr>
				<tr>
					<th>Bình quân services / số máy</th>
					<td class="text-center" id="average">`+ parseFloat(totalService / totalAtm).toFixed(2); +`</td>
				</tr>`;
				$('#tbl-data').append(html);
				localStorage.setItem("contractId", $('#contractId').val());
				localStorage.setItem("regionId", $('#regionId option:selected').val());
				localStorage.setItem("departmentId", $('#departmentId option:selected').val());
				localStorage.setItem("startTime", $('#startTime').val());
				localStorage.setItem("endTime", $('#endTime').val());
			}
		})

	})

	function loadDataInregion() {
		
		$.ajax({
			url: getContextPath() + "/statistical-region/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				regionId: $('#regionId').children("option:selected").val()==null?'':$('#regionId').children("option:selected").val(),
				departmentId:  $('#departmentId').children("option:selected").val()==null?'':$('#departmentId').children("option:selected").val(),

			}),
			success: function(result) {
				$('#departmentId').empty();
				let html = '';
				html += `
				
				<option value="" th:disabled th:selected="${departmentId != null ? 'false' : 'true'}" th:hidden>------ Tất cả đơn vị ------</option>
				
				`;
				for (let item of result) {

					html += `
				
				<option value="`+ item['id'] + `">
				`+ item['name'] + `
				</option>
				   
				`;
				}

				$('#departmentId').append(html);

			}
		})}


	
		loadDataInregion()
	$('#regionId').on('change', function() {
		loadDataInregion();
	})
	
	
	
	
	function loadDataIndepartmen() {
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
					if(departmentIditem=='')
					{
							html += `
				<option value=""  selected = 'selected' >------ Tất cả vùng miền ------</option>
				
				
				`;}
				for (let item of result) {

					html += `
				
				<option value="`+ item['id'] + `">
				`+ item['name'] + `
				</option>
				   
				`;
				}

				$('#regionId').append(html);

			}
		})}
		loadDataIndepartmen()
	$('#departmentId').on('change', function() {
		loadDataIndepartmen();
	
	})
	
	$(document).on('click', '.btn-expanded', function(){
		let i = $(this).attr("data-id");
		let length = $(this).attr("data-length");
		let trTag = $('.tr-' + i);
		let collapsedTag = $('#collapsed-' + i);
		let expandedTag = $('#expanded-' + i);
		
		trTag.removeAttr("hidden");
		$('.firstRow-' + i).attr("rowSpan", length);
		
		collapsedTag.removeAttr("hidden");
		expandedTag.attr("hidden", true);
		
	});
	
	$(document).on('click', '.btn-collapsed', function(){
		let i = $(this).attr("data-id");
		let trTag = $('.tr-' + i);
		let collapsedTag = $('#collapsed-' + i);
		let expandedTag = $('#expanded-' + i);
		
		for (let item of trTag) {
			if (item.getAttribute("data-index") > 3) {
				item.setAttribute("hidden", true);
			}
	    }
		
		$('.firstRow-' + i).attr("rowSpan", 5);
		
		expandedTag.removeAttr("hidden");
		collapsedTag.attr("hidden", true);
		
	});
});
