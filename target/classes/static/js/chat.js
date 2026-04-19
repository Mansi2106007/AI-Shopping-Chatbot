// ─── Auth Guard ───────────────────────────────────────────────────────────────
if (sessionStorage.getItem('loggedIn') !== 'true') {
  window.location.href = '/login.html';
}

const username = sessionStorage.getItem('username') || 'User';
document.getElementById('userName').textContent = username;
document.getElementById('userAvatar').textContent = username.charAt(0).toUpperCase();

// ─── State ────────────────────────────────────────────────────────────────────
let isBotTyping = false;

// ─── Init ─────────────────────────────────────────────────────────────────────
window.addEventListener('DOMContentLoaded', () => {
  addBotMessage(
    `👋 Welcome back, **${username}**!\n\nI'm your AI Shopping Assistant. I can help you find the best phones within your budget.\n\nJust say something like:\n• "I want a phone"\n• "Phones under 15000"\n• "Show Samsung phones"`,
    null
  );
  document.getElementById('messageInput').focus();
});

// ─── Send on Enter ────────────────────────────────────────────────────────────
document.getElementById('messageInput').addEventListener('keydown', e => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
});

// ─── Send Message ─────────────────────────────────────────────────────────────
async function sendMessage() {
  const input = document.getElementById('messageInput');
  const msg = input.value.trim();
  if (!msg || isBotTyping) return;

  input.value = '';
  addUserMessage(msg);
  showTyping();

  try {
    const res = await fetch('/chat?message=' + encodeURIComponent(msg));

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      removeTyping();
      addBotMessage('⚠️ ' + (errData.message || 'Something went wrong. Please try again.'), null);
      return;
    }

    const data = await res.json();
    removeTyping();

    if (data.type === 'products' && data.products && data.products.length > 0) {
      addBotMessage(data.message, data.products, data.followUp);
    } else {
      addBotMessage(data.message || "Sorry, I couldn't process that.", null);
    }
  } catch (err) {
    removeTyping();
    addBotMessage(
      '🔴 **Connection error.** Cannot reach the server.\n\nMake sure Spring Boot is running:\n`mvn spring-boot:run`',
      null
    );
    console.error('Chat error:', err);
  }
}

// ─── Quick action buttons (sidebar) ──────────────────────────────────────────
function sendQuick(msg) {
  const input = document.getElementById('messageInput');
  input.value = msg;
  sendMessage();
}

// ─── Render: User message ──────────────────────────────────────────────────────
function addUserMessage(text) {
  const body = document.getElementById('chatBody');
  const time = getTime();

  const row = document.createElement('div');
  row.className = 'msg-row user';
  row.innerHTML = `
    <div class="msg-avatar">👤</div>
    <div class="msg-content">
      <div class="bubble">${escapeHtml(text)}</div>
      <div class="msg-time">${time}</div>
    </div>
  `;
  body.appendChild(row);
  scrollBottom();
}

// ─── Render: Bot message ───────────────────────────────────────────────────────
function addBotMessage(text, products, followUp) {
  const body = document.getElementById('chatBody');
  const time = getTime();

  // Build product cards HTML
  let productHTML = '';
  if (products && products.length > 0) {
    const cards = products.map(p => {
      const price = Number(p.price).toLocaleString('en-IN');
      return `
        <div class="product-card">
          <div class="product-img">📱</div>
          <div class="product-info">
            <div class="product-name">${escapeHtml(p.name)}</div>
            <div class="product-brand">${escapeHtml(p.brand || '')}</div>
            <div class="product-desc">${escapeHtml(p.description || '')}</div>
          </div>
          <div class="product-price">₹${price}</div>
        </div>
      `;
    }).join('');

    productHTML = `<div class="product-grid">${cards}</div>`;
  }

  // Follow-up hint
  const followUpHTML = followUp
    ? `<div class="followup-hint">${escapeHtml(followUp)}</div>`
    : '';

  const row = document.createElement('div');
  row.className = 'msg-row bot';
  row.innerHTML = `
    <div class="msg-avatar">🤖</div>
    <div class="msg-content">
      <div class="bubble">
        <div class="bubble-text">${formatText(text)}</div>
        ${productHTML}
        ${followUpHTML}
      </div>
      <div class="msg-time">${time}</div>
    </div>
  `;
  body.appendChild(row);
  scrollBottom();
}

// ─── Render: Typing indicator ─────────────────────────────────────────────────
function showTyping() {
  isBotTyping = true;
  document.getElementById('sendBtn').disabled = true;

  const body = document.getElementById('chatBody');
  const row = document.createElement('div');
  row.className = 'msg-row bot typing-indicator';
  row.id = 'typingRow';
  row.innerHTML = `
    <div class="msg-avatar">🤖</div>
    <div class="msg-content">
      <div class="bubble">
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
        <div class="typing-dot"></div>
      </div>
    </div>
  `;
  body.appendChild(row);
  scrollBottom();
}

function removeTyping() {
  isBotTyping = false;
  document.getElementById('sendBtn').disabled = false;
  const el = document.getElementById('typingRow');
  if (el) el.remove();
}

// ─── Controls ─────────────────────────────────────────────────────────────────
function clearChat() {
  if (!confirm('Clear all messages and start fresh?')) return;
  document.getElementById('chatBody').innerHTML = '';
  addBotMessage('🗑 Chat cleared! How can I help you find the perfect product today?', null);
}

function logout() {
  if (!confirm('Sign out of AI Shopping Bot?')) return;
  sessionStorage.clear();
  window.location.href = '/login.html';
}

function toggleSidebar() {
  const sidebar = document.getElementById('sidebar');
  sidebar.classList.toggle('collapsed');
}

// ─── Utility ──────────────────────────────────────────────────────────────────
function scrollBottom() {
  const body = document.getElementById('chatBody');
  requestAnimationFrame(() => {
    body.scrollTop = body.scrollHeight;
  });
}

function getTime() {
  return new Date().toLocaleTimeString('en-IN', {
    hour: '2-digit',
    minute: '2-digit'
  });
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Convert **bold** and newlines to HTML
function formatText(text) {
  return escapeHtml(text)
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.*?)`/g, '<code style="background:rgba(0,212,255,.1);padding:2px 6px;border-radius:4px;font-family:monospace;font-size:12px">$1</code>')
    .replace(/\n/g, '<br>');
}
