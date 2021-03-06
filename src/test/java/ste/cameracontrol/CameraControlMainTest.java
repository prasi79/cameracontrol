/*
 * cameracontrol
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
import java.io.File;
import java.lang.reflect.Field;
import junit.framework.TestCase;

import ste.cameracontrol.ui.CameraControlWindow;

/**
 *
 * @author ste
 */
public class CameraControlMainTest extends TestCase {

    
    public CameraControlMainTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LibusbJava.init(new CanonEOS1000D(false));

        System.setProperty(Configuration.CONFIG_IMAGEDIR, "/tmp/cameracontrol");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private CameraControlWindow getWindow() throws Exception {
        Field f = CameraControlMain.class.getDeclaredField("window");
        f.setAccessible(true);

        return (CameraControlWindow)f.get(new CameraControlMain());
    }

    public void testControllerConfiguration() throws Exception {
        CameraControlMain cameraControl = new CameraControlMain();
        Configuration c = CameraController.getInstance().getConfiguration();
        assertNotNull(c.getImageDir());
        assertTrue((new File(c.getImageDir())).exists());
    }

    public void testCameraConnected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        CameraControlWindow window = getWindow();
        System.out.println("/testCameraConnected");
        Thread.sleep(100);
        System.out.println("testCameraConnected/");
        assertNotNull(window.status);
        assertTrue(window.cameraControlsEnabled);
    }

    public void testCameraDisconnected() throws Exception {
        //
        // we need to simulate a connection otherwise the status will not change
        //
        LibusbJava.init(new CanonEOS1000D(true));
        CameraControlWindow window = getWindow();
        Thread.sleep(100);
        window.status = null;
        LibusbJava.init(new CanonEOS1000D(false));
        Thread.sleep(100);
        assertNull(window.status);
        assertFalse(window.cameraControlsEnabled);
    }

    public void testCameraNameDetected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        CameraControlWindow window = getWindow();
        Thread.sleep(100);
        assertNotNull(window.status);
        assertTrue("found " + window.status, window.status.indexOf("1000D") >= 0);
    }

}
