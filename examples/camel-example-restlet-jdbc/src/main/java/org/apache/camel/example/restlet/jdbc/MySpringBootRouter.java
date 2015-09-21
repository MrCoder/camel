/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.restlet.jdbc;

import org.apache.camel.spring.boot.FatJarRouter;
import org.restlet.ext.spring.SpringServerServlet;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.HashMap;

@SpringBootApplication
@ImportResource(value = "common.xml")
public class MySpringBootRouter extends FatJarRouter {

    @Override
    public void configure() {
        from("timer://trigger").
                transform().simple("ref:myBean").
                to("log:out", "mock:test");
    }

    @Bean
    String myBean() {
        return "I'm Spring bean!";
    }

    @Bean
    public ServletRegistrationBean getServletRegistrationBean() {
        SpringServerServlet springServerServlet = new SpringServerServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(springServerServlet, "/rs/*");
        HashMap<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("org.restlet.component", "RestletComponent");
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

//    @Bean
//    public ContextLoaderListener getContextLoaderListener() {
//        return new org.springframework.web.context.ContextLoaderListener();
//    }

}
