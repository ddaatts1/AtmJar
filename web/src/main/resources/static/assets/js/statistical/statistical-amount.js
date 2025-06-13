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
						contractId: localStorage.getItem("contractId") !== null ? localStorage.getItem("contractId") : "",
						regionId: localStorage.getItem("regionId") !== null ? localStorage.getItem("regionId") : "",
						departmentId: localStorage.getItem("departmentId") !== null ? localStorage.getItem("departmentId") : "",
						startTime: localStorage.getItem("startTime") !== null ? localStorage.getItem("startTime") : "",
						endTime: localStorage.getItem("endTime") !== null ? localStorage.getItem("startTime") : ""
					}),
					success: function(result) {
						var blob = new Blob([result], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' });
						var URL = window.URL || window.webkitURL;
						var downloadUrl = URL.createObjectURL(blob);
						document.location = downloadUrl;

						toastr.success("Xuất báo cáo thành công!");
					}
				});
			}
		})
	})

	function loadDataInTable() {	
		localStorage.clear();
		$.ajax({
			url: getContextPath() + "/statistical-amount/search",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				contractId: $('#contractId').val(),
				regionId: $('#regionId option:selected').val(),
				departmentId: $('#departmentId option:selected').val(),
				startTime: $('#startTime').val(),
				endTime: $('#endTime').val()
			}),
			success: function(result) {
				localStorage.clear();
				$('#searchResult').empty();
				$('#btn-export').attr("hidden", true);



				if (result.length > 0) {
					$('#btn-export').removeAttr("hidden");
					$('#tota').show();


				} if (result.length > 0) {
					$('#searchResult').append('<div class="tbl_search_result"></div>');

					$('.tbl_search_result').append('<table class="table table-bordered table-hover" id="tbl-data"></table>')

					$('#tbl-data').append('<tr class="title"></tr>')

					$('.title').append('<th style="text-align: center;">Hợp đồng</th>\n')
						.append('<th style="text-align: center;">Vùng</th>\n')
						.append('<th style="text-align: center;">Đơn vị phụ trách</th>\n')
						.append('<th style="text-align: center;">Số lượng máy</th>\n')
						.append('<th style="text-align: center;">Số lần services</th>\n')
						.append('<th style="text-align: center;">Số linh kiện thay thế</th>\n')
						.append('<th style="text-align: center;">Bình quân services / số máy</th>\n')

				}
				else {
					$('#searchResult').append('<div class="tbl_search_result"><center><i>không tìm thấy báo cáo tổng lượng hợp đồng</i></center></div>');
				}

				let html = '';
				let totalAtm = 0;
				let totalService = 0;
				let totalAccessory = 0;
				let lengthaverage = 0;
				let countlength = 0;
				let average = 0;

				for (let item of result) {
					let result_child_1 = item["statisticalRegion"];
					for (let j = 0; j < result_child_1.length; j++) {
						let result_child_2 = result_child_1[j]["statisticalDepartment"];
						for (let k = 0; k < result_child_2.length; k++) {
							totalAtm += parseFloat(result_child_2[k]['totalAtm']);
							totalService += parseFloat(result_child_2[k]['totalService']);
							totalAccessory += parseFloat(result_child_2[k]['totalAccessory']);
							countlength += 1;
							lengthaverage += parseFloat(result_child_2[k]['average']);
							let col2 = '';
							if (k == 0) {
								col2 = `<td rowspan="` + result_child_2.length + `">` + result_child_1[j]['region'] + `</td>`;
							}
							let col1 = ``;
							if (k == 0 && j == 0) {
								col1 = `<td rowspan="` + item["countDepartment"] + `">` + item['contract'] + `</td>`;
							}
							html += `
								<tr>
									`+ col1 + `
									`+ col2 + `
									<td>`+ result_child_2[k]['department'] + `</td>
									<td style="text-align: center;">`+ result_child_2[k]['totalAtm'] + `</td>
									<td style="text-align: center;">`+ result_child_2[k]['totalService'] + `</td>
									<td style="text-align: center;">`+ result_child_2[k]['totalAccessory'] + `</td>
									<td style="text-align: center;">`+ result_child_2[k]['average'] + `</td>
								</tr>
							`;
						}
					}
				}
				if (result.length > 0) {
					html += `
						<tr id="tota"  style="background: #C0C0C0;">
							<td style="text-align: center;" colspan="3"><b>Tổng</b></td>
							<td style="text-align: center;"><b>`+ totalAtm + `</b></td>
							<td style="text-align: center;"><b>`+ totalService + `</b></td>
							<td style="text-align: center;"><b>`+ totalAccessory + `</b></td>
							<td style="text-align: center;"><b>`+ parseFloat(totalService / totalAtm).toFixed(2); +`</b></td>
						</tr>
					`;
				}
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
		loadDataInTable()
	})

});