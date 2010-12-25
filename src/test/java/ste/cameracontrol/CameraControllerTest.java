/*
 * Camera Control
 * Copyright (C) 2010 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */

package ste.cameracontrol;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ste.cameracontrol.event.ConnectedEventListener;

/**
 * Unit test for simple App.
 */
public class CameraControllerTest
        extends TestCase {

    private CameraController CONTROLLER = null;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CameraControllerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CameraControllerTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void fireCameraConnectionEvent() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("setConnected");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }

    public void testCameraIsConnected() {
        LibusbJava.init(new CanonEOS1000D(true));
        assertTrue(new CameraController().isConnected());
    }

    public void testCameraIsNotConnected() {
        LibusbJava.init(new CanonEOS1000D(false));
        assertFalse(new CameraController().isConnected());
    }

    public void testNoFireConnectionEvent() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        CONTROLLER = new CameraController();
        fireCameraConnectionEvent();

        //
        // We should have no errors
        //
    }

    public void testFireConnectionEvent() throws Exception {
        CONTROLLER = new CameraController();
        ConnectedEventListener[] listeners = {
                new ConnectedEventListener(),
                new ConnectedEventListener()
        };

        for (ConnectedEventListener l: listeners) {
            CONTROLLER.addCameraListener(l);
        }

        fireCameraConnectionEvent();

        for (ConnectedEventListener l: listeners) {
            assertNotNull(l.device);
        }
    }

    public void testStartCameraMonitor() throws Exception {
        ConnectedEventListener l = new ConnectedEventListener();

        LibusbJava.init(new CanonEOS1000D(false));
        CONTROLLER = new CameraController();
        CONTROLLER.addCameraListener(l);
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        assertNull(l.device);
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(l.device);
        //
        // It has to notify only changes
        //
        l.device = null;
        Thread.sleep(100);
        assertNull(l.device);
    }

    public void testStopCameraMonitor() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        CONTROLLER = new CameraController();
        ConnectedEventListener l = new ConnectedEventListener();

        //
        // By default the monitor does not run
        //
        CONTROLLER.addCameraListener(l);
        Thread.sleep(100);
        assertNull(l.device);

        //
        // Let's start the monitor now
        //
        l.device = null;
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        assertNotNull(l.device);

        //
        // Let's stop the monitor and detach the camera
        //
        l.device = null;
        LibusbJava.init(new CanonEOS1000D(false));
        CONTROLLER.stopCameraMonior();
        Thread.sleep(100);
        assertNull(l.device);
    }

    public void testDetectValidDevice() throws Exception  {
        CanonEOS1000D devinfo = new CanonEOS1000D(true);
        LibusbJava.init(devinfo);
        
        CONTROLLER = new CameraController();
        ConnectedEventListener l = new ConnectedEventListener();
        CONTROLLER.addCameraListener(l);
        fireCameraConnectionEvent();

        assertEquals(devinfo.getVendorId(), l.device.getVendorId());
        assertEquals(devinfo.getProductId(), l.device.getProductId());
    }

    public void _testShootOK() throws Exception  {
        new CameraController().shoot();
    }


}
