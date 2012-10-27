/**
 * Copyright (c) 2010, Mikhail Malygin, Ashirbaev Ildar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the IFMO nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.aphreet.c3.search.tika;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.io.IOException;
import java.util.Map;

public class TikaRunner {

    private final static Log logger = LogFactory.getLog(TikaRunner.class);

    public static void main(String[] args) {

        if (args.length >= 1) {
            if (args[0].equals("stop"))
                stop();
            else {
                if (args[0].equals("parse")) {
                    if (args.length >= 2) {
                        parse(args[1]);
                    } else {
                        System.out.println("No file specified");
                    }
                }
            }
        } else {
            start();
        }
    }

    public static void start() {
        //Starting instance

        @SuppressWarnings("unused")
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        logger.info("Tika server started");
    }

    public static void stop() {
        RmiProxyFactoryBean shutdownBeanProxy = new RmiProxyFactoryBean();
        shutdownBeanProxy.setServiceInterface(TikaControl.class);
        shutdownBeanProxy.setServiceUrl("rmi://localhost:1399/TikaControl");

        shutdownBeanProxy.afterPropertiesSet();

        TikaControl control = (TikaControl) shutdownBeanProxy.getObject();

        control.shutdown();

        System.out.println("Shutdown complete");
    }

    public static void parse(String filename) {
        RmiProxyFactoryBean shutdownBeanProxy = new RmiProxyFactoryBean();
        shutdownBeanProxy.setServiceInterface(TikaProvider.class);
        shutdownBeanProxy.setServiceUrl("rmi://localhost:1399/TikaProvider");

        shutdownBeanProxy.afterPropertiesSet();

        TikaProvider tika = (TikaProvider) shutdownBeanProxy.getObject();

        Map<String, String> metadata = tika.extractMetadata(filename);

        try {
            for (String key : metadata.keySet()) {
                System.out.print(key + " => ");

                System.out.write(metadata.get(key).getBytes("cp866"));
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
