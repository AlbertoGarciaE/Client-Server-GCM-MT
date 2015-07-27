$('document')
		.ready(
				function() {

					$('#input_notification')
							.change(
									function() {
										if ($('#input_notification').is(
												':checked') == true) {
											$('#input_notification_title')
													.prop('disabled', false);
											$('#input_notification_body').prop(
													'disabled', false);
											$('#input_notification_icon').prop(
													'disabled', false);
											$('#input_notification_sound')
													.prop('disabled', false);
											$('#input_notification_tag').prop(
													'disabled', false);
											$('#input_notification_color')
													.prop('disabled', false);
											$(
													'#input_notification_click_action')
													.prop('disabled', false);
											console.log('checked');
										} else {
											$('#input_notification_title')
													.prop('disabled', true);
											$('#input_notification_body').prop(
													'disabled', true);
											$('#input_notification_icon').prop(
													'disabled', true);
											$('#input_notification_sound')
													.prop('disabled', true);
											$('#input_notification_tag').prop(
													'disabled', true);
											$('#input_notification_color')
													.prop('disabled', true);
											$(
													'#input_notification_click_action')
													.prop('disabled', true);
											console.log('unchecked');
										}
									});

					// set empty json
					function clearEditor() {
						var json = {};
						editor.set(json);
					}

					// get json and parse to textarea
					function saveEditor() {
						var json = editor.get();
						var text = JSON.stringify(json)
						$("#form_http_json textarea#input_payload_data").val(
								text);
					}

					function addPlainDataField() {
						var text = $(
								"#form_http_plain textarea#input_payload_data")
								.val();
						var field = $("#input_plain_data_field_key").val()
								+ "="
								+ $("#input_plain_data_field_value").val();
						if (text != "") {
							text = text + "&" + field;
						} else {
							text = field;
						}
						$("#form_http_plain textarea#input_payload_data").val(
								text);
					}

					// Init color picker for JSON simple message
					$('.demo2').colorpicker();
					// JSON editor
					// create the editor
					var container = document
							.getElementById("dataEditorJsonSingle");
					var editor = new JSONEditor(container);
					$("#btn_clear_dataEditor").on("click", clearEditor);
					$("#btn_save_dataEditor").on("click", saveEditor);
					$("#btn_add_plain_data_field").on("click",
							addPlainDataField);
				});
