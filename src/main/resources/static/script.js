
let currentUser = null;

function showAuthTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));


    const tabs = document.querySelectorAll('.tab-btn');
    if (tab === 'login') tabs[0].classList.add('active');
    else tabs[1].classList.add('active');

    document.getElementById(`${tab}-form`).classList.add('active');
}

async function login() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }) // 'passwordHash' in DTO, but here sending raw for backend to check
        });

        if (response.ok) {
            currentUser = email;
            document.getElementById('user-email-display').textContent = currentUser;
            document.getElementById('auth-container').classList.remove('active');
            document.getElementById('dashboard-container').classList.add('active');
            showToast("Login Successful");
        } else {
            showToast("Login Failed: Invalid credentials");
        }
    } catch (error) {
        showToast("Error: " + error);
    }
}

async function register() {
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;

    try {
        const response = await fetch('/api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();
        if (response.ok) {
            showToast("Registration Successful! Please login.");
            showAuthTab('login');
        } else {
            showToast("Registration Failed: " + data.error);
        }
    } catch (error) {
        showToast("Error: " + error);
    }
}

function logout() {
    currentUser = null;
    document.getElementById('dashboard-container').classList.remove('active');
    document.getElementById('auth-container').classList.add('active');

    document.querySelectorAll('input').forEach(i => i.value = '');
}


function showView(viewName) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById(`${viewName}-view`).classList.add('active');


    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    if (viewName === 'inbox') {
        document.querySelectorAll('.nav-item')[0].classList.add('active');
        loadInboxEmails();
    }
    if (viewName === 'sent') {
        document.querySelectorAll('.nav-item')[1].classList.add('active');
        loadSentEmails();
    }
}

async function loadInboxEmails() {
    try {
        const listContainer = document.querySelector('#inbox-view .email-list');
        listContainer.innerHTML = '<div class="email-item"><span class="subject">Loading...</span></div>';

        const response = await fetch('/api/email/inbox');
        if (response.ok) {
            const emails = await response.json();
            listContainer.innerHTML = '';

            if (emails.length === 0) {
                listContainer.innerHTML = '<div class="email-item"><span class="subject">No emails in inbox.</span></div>';
            } else {
                emails.forEach(email => {
                    const item = document.createElement('div');
                    item.className = 'email-item';
                    item.onclick = () => viewEmail(email);
                    item.innerHTML = `
                        <span class="sender">${email.from}</span>
                        <span class="subject">${email.subject}</span>
                        <span class="date">${email.date}</span>
                    `;
                    listContainer.appendChild(item);
                });
            }
        } else {
            listContainer.innerHTML = '<div class="email-item"><span class="subject">Failed to load emails.</span></div>';
        }
    } catch (error) {
        console.error("Failed to load inbox", error);
        document.querySelector('#inbox-view .email-list').innerHTML = '<div class="email-item"><span class="subject">Error loading emails.</span></div>';
    }
}

function viewEmail(email) {
    document.getElementById('modal-subject').textContent = email.subject;
    document.getElementById('modal-from').textContent = "From: " + email.from;
    document.getElementById('modal-date').textContent = "Date: " + email.date;
    document.getElementById('modal-body').innerHTML = email.body; // Use innerHTML for HTML content

    document.getElementById('email-detail-modal').style.display = 'block';
}

function closeEmailModal() {
    document.getElementById('email-detail-modal').style.display = 'none';
}

// Close modal if clicked outside
window.onclick = function (event) {
    const modal = document.getElementById('email-detail-modal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

async function loadSentEmails() {
    try {
        const response = await fetch('/api/email/sent');
        if (response.ok) {
            const emails = await response.json();
            const listContainer = document.querySelector('#sent-view .email-list');
            listContainer.innerHTML = '';

            if (emails.length === 0) {
                listContainer.innerHTML = '<div class="email-item"><span class="subject">No sent emails found.</span></div>';
            } else {

                emails.reverse().forEach(email => {
                    const item = document.createElement('div');
                    item.className = 'email-item';
                    item.innerHTML = `
                        <span class="sender">To: ${email.to}</span>
                        <span class="subject">${email.subject}</span>
                        <span class="date">${email.timestamp}</span>
                    `;
                    listContainer.appendChild(item);
                });
            }
        }
    } catch (error) {
        console.error("Failed to load sent emails", error);
    }
}

async function sendEmail() {
    const to = document.getElementById('compose-to').value;
    const subject = document.getElementById('compose-subject').value;
    const body = document.getElementById('compose-body').value;

    if (!to || !subject || !body) {
        showToast("Please fill all fields");
        return;
    }

    showToast("Sending...");

    try {
        const response = await fetch('/api/email/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ to, subject, body })
        });

        const data = await response.json();
        if (response.ok) {
            showToast("Email Sent!");
            document.getElementById('compose-to').value = '';
            document.getElementById('compose-subject').value = '';
            document.getElementById('compose-body').value = '';
            showView('sent');
        } else {
            showToast("Failed: " + (data.error || "Unknown error"));
        }
    } catch (error) {
        showToast("Error: " + error);
    }
}

function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = "toast show";
    setTimeout(function () { toast.className = toast.className.replace("show", ""); }, 3000);
}
