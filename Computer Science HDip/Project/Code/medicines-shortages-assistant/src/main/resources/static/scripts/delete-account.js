document.addEventListener("DOMContentLoaded", function() {
    
    ////////////////////////////////////////////// DELETE PHARMACY ACCOUNT ////////////////////////////////////////
    
    // on clicking "Delete Account"
    document.getElementById("delete-form").addEventListener("submit", async function(event) {

        // use event.preventDefault to prevent page reload on button click
        event.preventDefault(); 

        // clear any previous message
        const messageContainer = document.getElementById("message-container");
        messageContainer.innerHTML = "";

        // call backend delete pharmacy API, passing in password
        const password = document.getElementById("password").value;
        
        try {
            
            // call backend delete account API
            const response = await fetch("http://localhost:8080/pharmacies/delete-account", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ password: password }),
                credentials: "include"  
            });

            // if the response from the API is ok
            messageContainer.style.display = "flex";
            if (response.status == 204) {
                // if account deletion is successful, display an alert
                alert("Account deleted successfully.");
                
                // sign the user out
                try {

                    const signOutResponse = await fetch('http://localhost:8080/pharmacies/sign-out', {
                        method: 'POST',
                        credentials: 'include',
                    });

                    if (signOutResponse.ok) {
                        // redirect to the home page on signing out
                        window.location.href = 'index.html';
                    } 
                    else {
                        messageContainer.style.display = "flex";
                        messageContainer.innerHTML = 'An error occurred. Failed to sign out.';
                        throw new Error('An error occurred. Failed to sign out.');
                    }
                } 
                catch (error) {
                    console.error('Error:', error);
                    messageContainer.style.display = "flex";
                    messageContainer.innerHTML = error.message;
                }

                // clear other messages, password field and delete account button
                const deleteForm = document.getElementById("delete-form");
                deleteForm.innerHTML = "";
            }
            else if(response.status == 404) {
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
            
        } catch (error) {
            // catch error thrown by try block and display it as an alert
            console.error("Error:", error);
            messageContainer.style.display = "flex";
            messageContainer.innerHTML = error.message;
        }

    });
    
});
