package com.example.awaz.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.databinding.ItemPostDetailActivityBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class ItemPostDetailActivity extends AppCompatActivity {
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ItemPostDetailActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItemPostDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        // Display the selected image in the gallery button
                        if (binding.galleryButton != null) {
                            binding.galleryButton.setImageURI(selectedImageUri);
                        }
                        Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set click listeners for profile-related views
        if (binding.postAuthorProfile != null) {
            binding.postAuthorProfile.setOnClickListener(v -> {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
            });
        }

        if (binding.postAuthor != null) {
            binding.postAuthor.setOnClickListener(v -> {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
            });
        }

        // Back button click listener
        if (binding.backArrow != null) {
            binding.backArrow.setOnClickListener(v -> finish());
        }

        // Profile icon click listener
        if (binding.imgProfile != null) {
            binding.imgProfile.setOnClickListener(v -> {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
            });
        }

        // Populate post data with full description
        Intent dataIntent = getIntent();
        if (dataIntent != null) {
            if (binding.postTitle != null) {
                String title = dataIntent.getStringExtra("postTitle");
                binding.postTitle.setText(title != null ? title : "No Title");
            }
            if (binding.postDescription != null) {
                String description = dataIntent.getStringExtra("postDescription");
                if (description != null) {
                    binding.postDescription.setText(description);
                    binding.postDescription.setMaxLines(Integer.MAX_VALUE);
                    binding.postDescription.setEllipsize(null);
                } else {
                    binding.postDescription.setText("No Description");
                }
            }
            if (binding.postTime != null) {
                String time = dataIntent.getStringExtra("postTime");
                binding.postTime.setText(time != null ? time : "No Time");
            }
        }

        // Scroll to comment section if requested
        if (dataIntent != null && dataIntent.getBooleanExtra("scroll_to_comments", false)) {
            if (binding.postDetailScroll != null && binding.commentsContainer != null) {
                binding.postDetailScroll.post(() -> binding.postDetailScroll.smoothScrollTo(0, binding.commentsContainer.getTop()));
            }
        }

        // Image click listeners for full-screen display
        if (binding.postImage1 != null) {
            binding.postImage1.setOnClickListener(v -> {
                Intent imageIntent = new Intent(this, FullscreenImageActivity.class);
                imageIntent.putExtra("image_resource", R.drawable.sample1);
                startActivity(imageIntent);
            });
        }

        if (binding.postImage2 != null) {
            binding.postImage2.setOnClickListener(v -> {
                Intent imageIntent = new Intent(this, FullscreenImageActivity.class);
                imageIntent.putExtra("image_resource", R.drawable.sample2);
                startActivity(imageIntent);
            });
        }

        if (binding.commentImage1 != null) {
            binding.commentImage1.setOnClickListener(v -> {
                Intent imageIntent = new Intent(this, FullscreenImageActivity.class);
                imageIntent.putExtra("image_resource", R.drawable.sample4);
                startActivity(imageIntent);
            });
        }

        if (binding.commentImage2 != null) {
            binding.commentImage2.setOnClickListener(v -> {
                Intent imageIntent = new Intent(this, FullscreenImageActivity.class);
                imageIntent.putExtra("image_resource", R.drawable.sample5);
                startActivity(imageIntent);
            });
        }

        // Three-dot icon click listener (optional, for future use)
        if (binding.threeDotIcon != null) {
            binding.threeDotIcon.setOnClickListener(v -> {
                Toast.makeText(this, "Options clicked", Toast.LENGTH_SHORT).show();
            });
        }

        // Gallery button click listener
        if (binding.galleryButton != null) {
            binding.galleryButton.setOnClickListener(v -> {
                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");
                imagePickerLauncher.launch(pickImageIntent);
            });
        }

        // Send button click listener
        if (binding.sendButton != null && binding.commentInput != null) {
            binding.sendButton.setOnClickListener(v -> {
                String commentText = binding.commentInput.getText().toString().trim();
                if (!commentText.isEmpty() || selectedImageUri != null) {
                    addComment(commentText, selectedImageUri);
                    binding.commentInput.setText("");
                    // Reset the gallery button to default icon and clear selectedImageUri
                    if (binding.galleryButton != null) {
                        binding.galleryButton.setImageResource(R.drawable.gallery_icon);
                    }
                    selectedImageUri = null;
                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a comment or select an image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addComment(String commentText, Uri imageUri) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View commentView = inflater.inflate(R.layout.item_comment, binding.dynamicComments, false);

        TextView authorName = commentView.findViewById(R.id.commentAuthor);
        TextView timeAgo = commentView.findViewById(R.id.commentTime);
        TextView commentTextView = commentView.findViewById(R.id.commentText);
        ImageView commentImage = commentView.findViewById(R.id.commentImage);
        ShapeableImageView authorProfile = commentView.findViewById(R.id.commentAuthorProfile);

        authorName.setText("Current User");
        timeAgo.setText("Just now");
        commentTextView.setText(commentText);

        if (imageUri != null) {
            commentImage.setVisibility(View.VISIBLE);
            commentImage.setImageURI(imageUri);
            // Make the comment image clickable to view in full screen
            commentImage.setOnClickListener(v -> {
                Intent imageIntent = new Intent(this, FullscreenImageActivity.class);
                imageIntent.putExtra("image_uri", imageUri.toString());
                startActivity(imageIntent);
            });
        } else {
            commentImage.setVisibility(View.GONE);
        }

        binding.dynamicComments.addView(commentView);

        if (binding.postDetailScroll != null) {
            binding.postDetailScroll.post(() -> binding.postDetailScroll.smoothScrollTo(0, binding.dynamicComments.getBottom()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }
}