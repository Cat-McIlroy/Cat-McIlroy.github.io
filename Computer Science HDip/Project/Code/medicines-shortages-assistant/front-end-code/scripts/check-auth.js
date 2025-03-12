document.addEventListener("DOMContentLoaded", async function() {
    
    // clear any previous message
    const messageContainer = document.getElementById("message-container");
    messageContainer.innerHTML = "";
    
    try {
        
        // call backend API to check if user is authenticated and therefore signed in
        const response = await fetch('http://localhost:8080/pharmacies/check-auth', {
            method: 'GET',
            credentials: 'include',
        });

        const signInOutDiv = document.getElementById("sign-in-out");
        const signInLink = document.getElementById("sign-in-out-link");

        if (response.ok) {
            
            const data = await response.json();
            
            // if user is authenticated and therefore signed in to an account
            if (data.authenticated) {
                
                // extract account username from API response
                const pharmacyName = data.pharmacyName;

                // create a flex container to align items correctly
                const containerDiv = document.createElement("div");
                containerDiv.style.display = "flex";
                containerDiv.style.justifyContent = "space-between";
                containerDiv.style.alignItems = "center";
                containerDiv.style.width = "100%";

                // create personalised "signed in as" message
                const accountInfoDiv = document.createElement("div");
                accountInfoDiv.style.display = "flex";
                accountInfoDiv.style.flexDirection = "column";
                accountInfoDiv.style.textAlign = "left";
                accountInfoDiv.style.marginRight = "10px";
                
                const signedInAsText = document.createElement("strong");
                signedInAsText.textContent = "Signed in as " + data.pharmacyName;
                accountInfoDiv.appendChild(signedInAsText);

                // create account dashboard link
                const dashboardLink = document.createElement("a");
                dashboardLink.href = "account-dashboard.html";
                dashboardLink.textContent = "My Account";
                dashboardLink.style.fontSize = "smaller";
                dashboardLink.style.marginTop = "4px";
                accountInfoDiv.appendChild(dashboardLink);
                
                // show the sign out link
                signInLink.textContent = "Sign out";
                // no link as the sign out is handled by the backend sign-out API
                signInLink.href = "#";

                signInLink.addEventListener('click', async function(event) {
                    // prevent default page refresh on clicking sign out link
                    event.preventDefault();

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
                    
                });

                // append account info and sign out link to container
                containerDiv.appendChild(accountInfoDiv);
                containerDiv.appendChild(signInLink);

                // replace existing content with updated layout
                signInOutDiv.innerHTML = "";
                signInOutDiv.appendChild(containerDiv);
                
            } 
            else {
                // if user not authenticated and therefore not signed in to an account, show the sign-in link
                signInLink.textContent = "Sign in";
                signInLink.href = "sign-in.html";
            }
            
        } 
        
        else {
            // handle error
            console.error("An error occurred. Could not check authentication status.");
        }
    } catch (error) {
        // handle any errors thrown by the fetch request
        console.error("Error:", error);
        messageContainer.style.display = "flex";
        messageContainer.innerHTML = "An error occurred while checking the user's authentication status.";
    }
    
});
