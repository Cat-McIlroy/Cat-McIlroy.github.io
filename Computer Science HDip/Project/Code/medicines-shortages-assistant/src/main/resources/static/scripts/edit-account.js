document.addEventListener("DOMContentLoaded", function() {
    
    // on submission of edit details form
    document.querySelector(".sign-in-form form").addEventListener("submit", async function(event) {
        
        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault(); 

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";
        
        // get the values from the fields
        const pharmacyName = document.getElementById("phar-name").value;
        const phoneNo = document.getElementById("contact").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("new-password").value;
        const currentPassword = document.getElementById("existing-password").value;
        
        // password validation
        // (?=.*[a-z]) → At least one lowercase letter
        // (?=.*[A-Z]) → At least one uppercase letter
        // (?=.*\d) → At least one number
        // (?=.*[@$!%*?&]) → At least one special character
        // [A-Za-z\d@$!%*?&]{12,} → Minimum 12 characters
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,}$/;

        // validate that password matches the passwordRegex
        if (!passwordRegex.test(password) && password !== "") {
            alert("Password must be at least 12 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character.");
            // if password is invalid, prevent form submission
            return; 
        }
        
        try {
            // call backend API to check if user is authenticated and therefore signed in
            const authResponse = await fetch('http://localhost:8080/pharmacies/check-auth', {
                method: 'GET',
                credentials: 'include',
            });
            
            if (authResponse.ok) {
                const data = await authResponse.json();

                // if user is authenticated and therefore signed in to an account
                if (data.authenticated) {

                    console.log(data);
                    // extract remaining details from API response
                    const psiRegNo = data.psiRegNo;
                    const address = data.address;
                    const eircode = data.eircode;
                    
                    // construct json payload
                    const accountDetails = {
                        pharmacy: {
                            psiRegNo,
                            pharmacyName,
                            address,
                            eircode,
                            phoneNo,
                            email,
                            password
                        },
                        currentPassword
                    };

                    try {
                        // call backend edit account details API
                        const editResponse = await fetch("http://localhost:8080/pharmacies/edit-account-details", {
                            method: "PATCH",
                            headers: {
                                "Content-Type": "application/json"
                            },
                            body: JSON.stringify(accountDetails),
                            credentials: "include"  
                        });

                        // display message container
                        messageContainer.style.display = "flex";
                        if (editResponse.ok) {
                            // if account details are edited successfully, hide form, update pharmacy name if necessary and display success message
                            document.querySelector(".sign-in-form").style.display = "none";
                            if(pharmacyName !== ""){
                                document.getElementById("account-name").textContent = "Signed in as " + pharmacyName;
                            }
                            messageContainer.innerHTML = "Account details changed successfully.";
                            messageContainer.classList.add("form-buttons");
                                    
                            // create "Back" button
                            const backButton = document.createElement("button");
                            backButton.style.marginTop = "20px";
                            backButton.textContent = "Back";
                            backButton.addEventListener("click", () => {
                                window.location.href = "account-dashboard.html";
                            });

                            // add the button to the container
                            messageContainer.appendChild(backButton);
                        } else {
                            // handle different response statuses
                            if (editResponse.status == 404) {
                                messageContainer.innerHTML = "No account found with this email.";
                            } else if (editResponse.status == 401) {
                                messageContainer.innerHTML = "Incorrect password. Please try again.";
                            } else {
                                messageContainer.innerHTML = "An error occurred. Please try again.";
                                const responseData = await editResponse.json();
                                throw new Error(responseData.error || "An error occurred. Please try again.");
                            }
                        }
                    } catch (error) {
                        console.error("Error:", error);
                        messageContainer.style.display = "flex";
                        messageContainer.innerHTML = error.message;
                    }
                } else {
                    // user is not authenticated
                    console.error("Authentication failed");
                    messageContainer.style.display = "flex";
                    messageContainer.innerHTML = "You need to be signed in to edit account details.";
                }
            } else {
                // if the check-auth API call failed
                console.error("An error occurred. Could not check authentication status.");
                messageContainer.style.display = "flex";
                messageContainer.innerHTML = "An error occurred while checking the user's authentication status.";
            }
        } catch (error) {
            // handle any errors thrown by the fetch request
            console.error("Error:", error);
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = "An unexpected error occurred.";
        }
    });
});
