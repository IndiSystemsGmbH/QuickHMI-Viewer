package de.indisystems.qhmiviewer.helper;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.teamdev.jxbrowser.chromium.internal.Environment;
/*
Copyright 2019 Indi.Systems GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

public class KeyHook {
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardHook;
    private static User32 lib;

    public static void blockWindowsKey() {
        if(Environment.isWindows()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    lib = User32.INSTANCE;
                    HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
                    keyboardHook = new LowLevelKeyboardProc() {
                        public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                            if (nCode >= 0) {
                                switch (info.vkCode){
                                    case 0x5B: //Left WIN
                                    case 0x5C: //Right WIN
                                    case 0xA4: //Left ALT
                                    case 0xA5: //Right ALT
                                    case 0xA2: //Left Control
                                    case 0xA3: //Right Control
                                        return new LRESULT(1);
                                    default: //do nothing
                                }
                            }
                            return lib.CallNextHookEx(hhk, nCode, wParam, null);
                        }
                    };
                    hhk = lib.SetWindowsHookEx(13, keyboardHook, hMod, 0);

                    // This bit never returns from GetMessage
                    int result;
                    MSG msg = new MSG();
                    while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                        if (result == -1) {
                            break;
                        } else {
                            lib.TranslateMessage(msg);
                            lib.DispatchMessage(msg);
                        }
                    }
                    lib.UnhookWindowsHookEx(hhk);
                }
            }).start();
        }
    }

    public static void unblockWindowsKey() {
        if (Environment.isWindows() && lib != null) {
            lib.UnhookWindowsHookEx(hhk);
        }
    }
}
