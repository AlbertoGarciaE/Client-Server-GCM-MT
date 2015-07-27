$('#input_notification').change(function() {
	if ($('#input_notification').is(':checked') == true) {
		$('#input_notification_title').prop('disabled', false);
		$('#input_notification_body').prop('disabled', false);
		$('#input_notification_icon').prop('disabled', false);
		$('#input_notification_sound').prop('disabled', false);
		$('#input_notification_tag').prop('disabled', false);
		$('#input_notification_color').prop('disabled', false);
		$('#input_notification_click_action').prop('disabled', false);
		console.log('checked');
	} else {
		$('#input_notification_title').prop('disabled', true);
		$('#input_notification_body').prop('disabled', true);
		$('#input_notification_icon').prop('disabled', true);
		$('#input_notification_sound').prop('disabled', true);
		$('#input_notification_tag').prop('disabled', true);
		$('#input_notification_color').prop('disabled', true);
		$('#input_notification_click_action').prop('disabled', true);
		console.log('unchecked');
	}
});