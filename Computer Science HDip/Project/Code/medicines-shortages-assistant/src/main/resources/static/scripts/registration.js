document.addEventListener("DOMContentLoaded", function() {
    
    // on submission of registration form
    document.querySelector(".sign-in-form form").addEventListener("submit", async function(event) {
        
        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault(); 

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";
        
        // get the values from the fields
        const pharmacyName = document.getElementById("phar-name").value;
        const psiRegNo = document.getElementById("reg-number").value;
        const address1 = document.getElementById("address-1").value;
        const address2 = document.getElementById("address-2").value || "";
        const address3 = document.getElementById("address-3").value || "";
        let address = "";
        
        // concatenate address lines into one string
        if (address2 == "" && address3 == "") {
            address = address1; 
        }
        else if (address2 != "" && address3 == "") {
            address = address1 + ", " + address2;
        }
        else if (address2 != "" && address3 != "") {
            address = address1 + ", " + address2 + ", " + address3;
        }
        
        const eircode = document.getElementById("eircode").value.replace(/\s+/g, '');
        const phoneNo = document.getElementById("contact").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        
        // password validation
        // (?=.*[a-z]) → At least one lowercase letter
        // (?=.*[A-Z]) → At least one uppercase letter
        // (?=.*\d) → At least one number
        // (?=.*[@$!%*?&]) → At least one special character
        // [A-Za-z\d@$!%*?&]{12,} → Minimum 12 characters
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,}$/;

        // validate that password matches the passwordRegex
        if (!passwordRegex.test(password)) {
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = "Password must be at least 12 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character.";
            // if password is invalid, prevent form submission
            return; 
        }

        // construct json payload
        const accountDetails = {
            pharmacyName,
            psiRegNo,
            address,
            eircode,
            phoneNo,
            email,
            password
        };

        try {
            
            // call backend register API
            const response = await fetch("http://localhost:8080/pharmacies/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(accountDetails),
                credentials: "include"  
            });

            // if the response from the API is ok
            if (response.ok) {
                
                // if registration is successful, get the credential values required for sign-in
                const email = document.getElementById("email").value;
                const password = document.getElementById("password").value;

                // construct json payload
                const credentials = {
                    email: email,
                    password: password
                };
                
                try {
                    
                    // sign-in the newly registered account by calling the backend sign-in API
                    const response = await fetch("http://localhost:8080/pharmacies/sign-in", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(credentials),
                        credentials: "include",
                        mode: 'cors'

                    });

                if (response.ok) {
                    const pharmacyData = await response.json();
                    // if sign in is successful, redirect to account dashboard
                    window.location.href = "account-dashboard.html";
                } 
                    
                else {
                    messageContainer.style.display = "flex";
                    // extract the error message from the sign-in API and throw it as a new error
                    // or if it is an undefined error, display generic error message
                    if(response.status == 404) {
                        messageContainer.innerHTML = "No account found with this email.";
                    }
                    else if(response.status == 401) {
                        messageContainer.innerHTML = "Incorrect password. Please try again.";
                    }
                    else{
                        messageContainer.innerHTML = "An error occurred. Please try again.";
                        const responseData = await response.json();
                        throw new Error(responseData.error || "An error occurred. Please try again.");
                    }
                }
            
                } catch (error) {
                    // catch error thrown by try block and display it as an alert
                    console.error("Error:", error);
                    messageContainer.style.display = "flex";
                    messageContainer.innerHTML = error.message;
                }
            } 
            
            else {
                // extract the error message from the registratiom API and throw it as a new error
                // or if it is an undefined error, display generic error message
                const responseData = await response.json();
                throw new Error(responseData.error || "An error occurred. Please try again.");
            }
            
        } catch (error) {
            // catch error thrown by try block and display it as an alert
            console.error("Error:", error);
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = error.message;
        }
        
    });
    
});
