/*
 * Copyright (c) 2021, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.attachextended.fingerprint.impl;

import com.gluonhq.attachextended.fingerprint.FingerprintService;
import com.gluonhq.attachextended.fingerprint.AuthenticationResult;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class IOSFingerprintService implements FingerprintService {

    private ReadOnlyObjectWrapper<AuthenticationResult> authenticationResultWrapper = new ReadOnlyObjectWrapper<>();

    static {
        System.loadLibrary("IOSFingerprintService"); // Load the native library
    }

    @Override
    public void authenticate() {
        authenticateWithFingerprint(); // Adapt this method for property-based result handling
    }

    @Override
    public ReadOnlyObjectProperty<AuthenticationResult> authenticationResultProperty() {
        return authenticationResultWrapper.getReadOnlyProperty();
    }

    // Adapted native method declaration
    private native void authenticateWithFingerprint();

    // Methods called from native code to update the authentication result
    private void onAuthenticationSuccess() {
        authenticationResultWrapper.set(new AuthenticationResult(true, "Authentication Successful"));
    }

    private void onAuthenticationFailure(String errorMessage) {
        authenticationResultWrapper.set(new AuthenticationResult(false, errorMessage));
    }
}
