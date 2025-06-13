function deleteUser(username) {
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
		location.href = getContextPath() + '/user/delete?username=' + username;
	  }
	})
}

function removeDevice(username) {
	Swal.fire({
		title: "Bạn có chắc không?",
        text: "Thiết bị đang sử dụng sẽ bị xóa khỏi kho dữ liệu!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Không"
	}).then((data) => {
	  if (data.isConfirmed) {
		$.ajax({
			url: getContextPath() + '/user/removeDevice',
			type: 'POST',
			contentType: "application/json",
	        data: JSON.stringify({
				username: username
			}),
			success: function(result) {
				$('#td-' + username).empty();
				toastr.success(result);
			},
			error: function(err) {
				console.log(err);
			}
		});
	  }
	})
}