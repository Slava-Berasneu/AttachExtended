import LocalAuthentication

@objc public class BiometricAuth: NSObject {

    private let context = LAContext()
    private var error: NSError?

    // Make the typealias public
    public typealias AuthenticationResult = (Bool, String?) -> Void

    @objc public func authenticateUser(completion: @escaping AuthenticationResult) {
        guard context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            completion(false, "Touch ID not available or not enrolled.")
            return
        }

        let reason = "Authenticate using your Touch ID"

        context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: reason) { success, authenticationError in
            DispatchQueue.main.async {
                if success {
                    completion(true, nil)
                } else {
                    let errorMessage = authenticationError?.localizedDescription ?? "Failed to authenticate"
                    completion(false, errorMessage)
                }
            }
        }
    }
}
