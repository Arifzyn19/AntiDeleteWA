package com.private.antideletewa.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.private.antideletewa.R
import com.private.antideletewa.databinding.ActivityMainBinding
import com.private.antideletewa.ui.adapter.MessageAdapter
import com.private.antideletewa.ui.viewmodel.MainViewModel

/**
 * MainActivity - UI utama aplikasi
 * Menampilkan list pesan yang tersimpan dari notifikasi WhatsApp
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MessageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        observeMessages()
        checkNotificationPermission()
    }
    
    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Private Messages"
    }
    
    /**
     * Setup RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = MessageAdapter()
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
        
        // Click listener untuk item (optional - untuk future detail view)
        adapter.onItemClick = { message ->
            // Untuk sekarang hanya toast, bisa dikembangkan jadi detail view
            Toast.makeText(
                this,
                "From: ${message.sender}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Observe messages dari ViewModel
     */
    private fun observeMessages() {
        viewModel.allMessages.observe(this) { messages ->
            if (messages.isEmpty()) {
                // Tampilkan empty state
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                // Tampilkan RecyclerView
                binding.emptyStateLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(messages)
            }
        }
    }
    
    /**
     * Cek apakah notification access sudah diaktifkan
     */
    private fun checkNotificationPermission() {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        
        if (!enabledListeners.contains(packageName)) {
            // Notification access belum diaktifkan, tampilkan dialog
            showNotificationPermissionDialog()
        }
    }
    
    /**
     * Dialog untuk meminta notification access
     */
    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Access Required")
            .setMessage("Aplikasi ini memerlukan akses notifikasi untuk menyimpan pesan WhatsApp.\n\n" +
                    "Silakan aktifkan 'Anti Delete WA' di pengaturan Notification Access.")
            .setPositiveButton("Open Settings") { _, _ ->
                openNotificationSettings()
            }
            .setNegativeButton("Later", null)
            .setCancelable(false)
            .show()
    }
    
    /**
     * Buka settings notification access
     */
    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
    
    /**
     * Create options menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    /**
     * Handle menu item click
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                showClearAllDialog()
                true
            }
            R.id.action_notification_settings -> {
                openNotificationSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Dialog konfirmasi hapus semua pesan
     */
    private fun showClearAllDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear All Messages")
            .setMessage("Are you sure you want to delete all saved messages?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAllMessages()
                Toast.makeText(this, "All messages deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
