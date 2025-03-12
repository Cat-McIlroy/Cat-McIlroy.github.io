document.addEventListener("DOMContentLoaded", function() {
    
    document.querySelector(".sign-in-form form").addEventListener("submit", async function(event) {
        
        // use event.preventDefault to prevent page reload on form submission
        event.preventDefault();
        
        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        // construct json payload
        const credentials = {
            email: email,
            password: password
        };

        try {
            
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
        
    });
    
});