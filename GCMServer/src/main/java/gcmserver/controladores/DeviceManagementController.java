/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gcmserver.controladores;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Servlet that unregisters a device, whose registration id is identified by
 * {@link #PARAMETER_REG_ID}.
 * <p>
 * The client app should call this servlet everytime it receives a
 * {@code com.google.android.c2dm.intent.REGISTRATION} with an
 * {@code unregistered} extra.
 */
@SuppressWarnings("serial")
@Controller
@RequestMapping("/deviceManagement")
public class DeviceManagementController  {

  private static final String PARAMETER_REG_ID = "regId";
//DeviceManager.class

  @RequestMapping(value="/unregisterDevice", method=RequestMethod.GET)
  protected void unregisterDevice(@RequestParam String regId, HttpSession sesion, Model modelo) {
    /*
     * TODO Unregister a device
     */
  }

  @RequestMapping(value="/registerDevice", method=RequestMethod.GET)
  protected void registerDevice(@RequestParam String regId, HttpSession sesion, Model modelo) {
    /*
     * TODO register a device
     */
  }
  
  
}
