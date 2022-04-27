package com.ifcompany.bookreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.bumptech.glide.Glide
import com.ifcompany.bookreview.databinding.ActivityDetailBinding
import com.ifcompany.bookreview.model.Book
import com.ifcompany.bookreview.model.Review

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = getAppDatabase(this)

        val bookModel = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = bookModel?.title.orEmpty()

        Glide
            .with(binding.coverImageView.context)
            .load(bookModel?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        binding.descriptionTextView.text = bookModel?.description.orEmpty()

        Thread{
            var review = db.reviewDao().getOneReview(bookModel?.id?.toInt()?:0)
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveButton.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        bookModel?.id?.toInt(),
                        binding.reviewEditText.text.toString()
                    )
                )

            }.start()
        }
    }
}