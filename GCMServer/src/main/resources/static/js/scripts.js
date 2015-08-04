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
					// set empty json
					function clearEditor2() {
						var json = {};
						editor2.set(json);
					}

					// get json and parse to textarea
					function saveEditor2() {
						var json = editor2.get();
						var text = JSON.stringify(json)
						$("#form_topic_message textarea#input_payload_data")
								.val(text);
					}

					// set empty json
					function clearEditor3() {
						var json = {};
						editor2.set(json);
					}

					// get json and parse to textarea
					function saveEditor3() {
						var json = editor3.get();
						var text = JSON.stringify(json)
						$("#form_group_message textarea#input_payload_data")
								.val(text);
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

					var container2 = document
							.getElementById("dataEditorTopicMessage");
					var editor2 = new JSONEditor(container2);

					var container3 = document
							.getElementById("dataEditorGroupMessage");
					var editor3 = new JSONEditor(container3);

					// Link handler to the buttons
					$("#form_group_message #btn_clear_dataEditor").on("click",
							clearEditor3);
					$("#form_group_message #btn_save_dataEditor").on("click",
							saveEditor3);

					$("#form_topic_message #btn_clear_dataEditor").on("click",
							clearEditor2);
					$("#form_topic_message #btn_save_dataEditor").on("click",
							saveEditor2);

					$("#form_http_json #btn_clear_dataEditor").on("click",
							clearEditor);
					$("#form_http_json #btn_save_dataEditor").on("click",
							saveEditor);

					$("#form_http_plain #btn_add_plain_data_field").on("click",
							addPlainDataField);

				});
